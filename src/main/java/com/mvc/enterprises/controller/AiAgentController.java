package com.mvc.enterprises.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.enterprises.entities.BatchRecords;
import com.mvc.enterprises.entities.ComplianceResultsDto;
import com.mvc.enterprises.entities.PageResponseDto;
import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.utils.ILConstants;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/aiAgent")
public class AiAgentController {

	private static final Logger _LOGGER = LoggerFactory.getLogger(AiAgentController.class);

	private final RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public AiAgentController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Get login page
	 * 
	 * @param model
	 * @return
	 */
    @RequestMapping(
    		value = "/showAiAgent",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
	public String showAiAgentCompliance(Model model, HttpServletRequest request) throws Exception {
		return "aiAgent/aiAgentHome";
	}

	/**
	 * Show ai-agent You are a pharmaceutical GMP auditor. Analyze the following
	 * document and provide: 1. Regulatory Compliance 2. Validation 3. Equipment
	 * Calibration 4. Sample Preparation 5. Data Analysis 6. Documentation 7.
	 * Training 8. Revision Control
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/showAiAgentResult", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String showAiAgentResult(@RequestPart("file") MultipartFile file, Model model, HttpServletRequest request) {
		try {
			
			String result = callAiService(file, request);

			// Pass the result to JSP
			String formatted = result.replace("\n", "<br/>");

			model.addAttribute("aiResult", formatted);
			model.addAttribute("msg", "File processed successfully!");

		} catch (Exception exp) {
			_LOGGER.error("AI Agent processing failed.......", exp);
			model.addAttribute("error", "Unable to process file.");
		}

		return "aiAgent/aiAgentHome";
	}

	/**
	 * Get login page
	 * 
	 * @param model
	 * @return
	 */
    @RequestMapping(
    		value = "/showAiAgentProcessBatch",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
	public String showAiAgentProcessBatch(Model model, HttpServletRequest request) throws Exception {
		return "aiAgent/aiAgentProcessBatchHome";
	}

	@PostMapping("/aiAgentProcessBatch")
	public String aiAgentProcessBatch(@RequestParam("batchNo") String batchNo,
			@RequestParam("productName") String productName, @RequestParam("file") MultipartFile file,
			HttpSession session, Model model) {

		try {
			// 1️. Save batch record (optional)
			BatchRecords batch = new BatchRecords();
			batch.setBatchNo(batchNo);
			batch.setProductName(productName);
			User user = (User) session.getAttribute(Utils.getSessionLoginUserIdKey());
			batch.setUploadedBy(user.getUserName());
			//AI Agent rest is setting up
			//batch.setUploadDate(LocalDateTime.now());
			batch.setStatus("Pending");
			// batch = batchRecordService.save(batch); // save to batch_records

			// 2️. Call microservice (Spring Boot) to process the PDF
			String result = callAiProcessBatchMicroservice(file, batch, session);

			// Pass the result to JSP
			String formatted = result.replace("\n", "<br/>");

			model.addAttribute("aiResult", formatted);
			model.addAttribute("msg", "File processed and regulatory compliance are saved successfully!");

		} catch (Exception exp) {
			_LOGGER.error("AI batch processing failed:", exp);
			model.addAttribute("error", "AI batch processing failed: " + exp.getMessage());
		}

		return "aiAgent/aiAgentProcessBatchHome";
	}

	@RequestMapping(
    		value = "/showAiAgentDashboard",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
	public String showDashboard(
			@RequestParam(defaultValue = "0") int page,
			Model model, 
			HttpServletRequest request) throws Exception {
		
	    List<BatchRecords> batches = getRestAllBatchRecords(request.getSession(), page);
	    
	    if(batches != null && batches.size() > 0) {
		    List<Long> batchIds = batches.stream()
		            .map(BatchRecords::getId)
		            .toList();
		    
		    List<ComplianceResultsDto> complianceList  = getRestAllComplianceResults(request.getSession(), batchIds, page);
		    
		    //Convert list to Map<BatchId, ComplianceResults>
		    Map<Long, ComplianceResultsDto> complianceMap = complianceList
		            .stream()
		            .collect(Collectors.toMap(ComplianceResultsDto::getBatchId, Function.identity()));
	
		    model.addAttribute("batches", batches);
		    model.addAttribute("complianceMap", complianceMap);
	    }

	    return "aiAgent/aiAgentDashboardHome"; // dashboard.jsp
	}

	/**
	 * Return to IL home
	 * 
	 * @param session
	 * @return
	 */
	@PostMapping("/returnILHome")
	public String returnILHome(HttpSession session) {
		if (session == null) {
			return "forward:/login"; // forward if not logged in
		}
		User user = (User) session.getAttribute(Utils.getSessionLoginUserIdKey());
		if (user == null) {
			return "forward:/login"; // forward if not logged in
		}
		return "ilHome";
	}

	// Helper to call REST controller
	private String callAiService(MultipartFile file, HttpServletRequest request) throws Exception {

		String url = ILConstants.MICROSERVICE_RESTFUL_AI_AGENT_URL + "/upload-pdf";

		InputStreamResource resource = new InputStreamResource(file.getInputStream()) {

			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}

			@Override
			public long contentLength() {
				return file.getSize();
			}
		};

		// Prepare multipart body
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", resource);

		// Get JWT token from session
		String token = (String) request.getSession().getAttribute(Utils.getTokenKey());

		// Set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		if (token != null) {
			headers.set("Authorization", "Bearer " + token);
		}

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// Call AI microservice
		// ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity,
		// Map.class);
		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

		String json = response.getBody();

		// _LOGGER.info("json:"+json);;
		JsonNode root = objectMapper.readTree(json);

		JsonNode reportNode = root.get("report");

		if (reportNode != null && reportNode.isTextual()) {

			String innerJson = reportNode.asText();

			// Parse inner JSON
			JsonNode innerRoot = objectMapper.readTree(innerJson);

			JsonNode finalReport = innerRoot.get("report");

			if (finalReport != null) {
				return finalReport.asText();
			}
		}

		return "No report found from model.";
	}

	// Helper to call REST controller
	private String callAiProcessBatchMicroservice(MultipartFile file, BatchRecords batchRecords, HttpSession session)
			throws Exception {

		String url = ILConstants.MICROSERVICE_RESTFUL_AI_AGENT_URL + "/upload-batch-process-pdf";

		/*
		 * InputStreamResource resource = new InputStreamResource(file.getInputStream())
		 * {
		 * 
		 * @Override public String getFilename() { return file.getOriginalFilename(); }
		 * 
		 * @Override public long contentLength() { return file.getSize(); } };
		 */
		// 1. Save MultipartFile to temp file
		File tempFile = File.createTempFile("upload-", ".pdf");
		file.transferTo(tempFile); // Streams file to disk

		// Convert BatchRecords → JSON
		String batchJson = objectMapper.writeValueAsString(batchRecords);

		// 2. Prepare multipart body
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(tempFile));
		body.add("batchRecords", batchJson);

		// Get JWT token from session
		String token = (String) session.getAttribute(Utils.getTokenKey());

		// 4. Set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		if (token != null) {
			headers.set("Authorization", "Bearer " + token);
		}

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		// Call AI microservice
		// ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity,
		// Map.class);
		ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

		String json = response.getBody();

		// _LOGGER.info("json:"+json);;

		ObjectMapper mapperJson = new ObjectMapper();
		JsonNode root = mapperJson.readTree(json);

		JsonNode reportNode = root.get("report");

		if (reportNode != null && reportNode.isTextual()) {

			String innerJson = reportNode.asText();

			// Parse inner JSON
			JsonNode innerRoot = objectMapper.readTree(innerJson);

			JsonNode finalReport = innerRoot.get("report");

			if (finalReport != null) {
				return finalReport.asText();
			}
		}

		return "No report found from model in bacth processing.";
	}
	
	/**
	 * Get all batch records
	 * @return
	 */

	private List<BatchRecords> getRestAllBatchRecords(HttpSession session, int page) throws Exception {
		_LOGGER.info(">>> Inside getRestAllBatchRecords. <<<");
		
		String urlBatchRecords = ILConstants.MICROSERVICE_RESTFUL_AI_AGENT_URL + "/batch-records";
		
		ResponseEntity<PageResponseDto<BatchRecords>> response = null;
		
		try {
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
			
			_LOGGER.info(">>> Inside getRestAllBatchRecords. before calling microservice.<<<");
			
			String urlBuilder = UriComponentsBuilder
			        .fromHttpUrl(urlBatchRecords)
			        .queryParam("page", 0)
			        .queryParam("size", 200)
			        .queryParam("sort", "productName")
			        .toUriString();
				
				response =
				    restTemplate.exchange(
				    		urlBuilder,
				            HttpMethod.GET,
				            entity,
				            new ParameterizedTypeReference<PageResponseDto<BatchRecords>>() {}
				    );	
			_LOGGER.info(">>> Inside getRestAllBatchRecords. after calling microservice.<<<");
		} catch (Exception exp) {
			_LOGGER.info(">>> Inside getRestAllBatchRecords exception. <<< "+exp.toString());
			throw new Exception("Network Authentication Required");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			_LOGGER.info(">>> Inside getRestAllBatchRecords. <<< Unauthorized");
			throw new Exception("Unauthorized");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			_LOGGER.info(">>> Inside getRestAllBatchRecords. <<< Not Found<<< ");
			throw new Exception("Not Found");
		}
		
		return response.getBody().getContent();
	} 
	
	/**
	 * Get all compliance result
	 * @return
	 */
	private List<ComplianceResultsDto> getRestAllComplianceResults(
	        HttpSession session,
	        List<Long> batchIds,
	        int page) throws Exception {

	    _LOGGER.info(">>> Inside getRestAllComplianceResults. <<<");

	    String url = ILConstants.MICROSERVICE_RESTFUL_AI_AGENT_URL + "/compliance-results";

	    ResponseEntity<PageResponseDto<ComplianceResultsDto>> response;

	    try {
	        HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);

	        _LOGGER.info(">>> Before calling compliance microservice <<<");

	        UriComponentsBuilder builder = UriComponentsBuilder
	                .fromHttpUrl(url)
	                .queryParam("page", page)     // ✅ use page param
	                .queryParam("size", 200)
	                .queryParam("sort", "riskLevel");

	        // ✅ Add batch IDs one by one
	        if (batchIds != null && !batchIds.isEmpty()) {
	            for (Long id : batchIds) {
	                builder.queryParam("batchRecordIds", id);
	            }
	        }

	        String urlBuilder = builder.toUriString();
	        
	        _LOGGER.info("Calling URL: " + urlBuilder);

	        response = restTemplate.exchange(
	                urlBuilder,
	                HttpMethod.GET,
	                entity,
	                new ParameterizedTypeReference<PageResponseDto<ComplianceResultsDto>>() {}
	        );

	        _LOGGER.info(">>> After calling compliance microservice <<<");

	    } catch (Exception exp) {
	        _LOGGER.error("Error in getRestAllComplianceResults", exp);
	        throw new Exception("Network Authentication Required");
	    }

	    if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
	        throw new Exception("Unauthorized");
	    }

	    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
	        throw new Exception("Not Found");
	    }

	    return response.getBody().getContent();
	}
	
	/**
	 * Set JWT token to the HttpHeaders
	 * 
	 * @param <T>
	 * @param session
	 * @param body
	 * @return
	 */
	private <T> HttpEntity<T> getJwtTokenToHttpRequest(HttpSession session, T body) {

	    String token = (String) session.getAttribute(Utils.getTokenKey());

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    if (token != null) {
	        headers.set("Authorization", "Bearer " + token);
	    }

	    return new HttpEntity<>(body, headers);
	}
}
