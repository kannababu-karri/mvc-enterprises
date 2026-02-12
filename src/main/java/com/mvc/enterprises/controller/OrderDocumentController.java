package com.mvc.enterprises.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mvc.enterprises.entities.Manufacturer;
import com.mvc.enterprises.entities.OrderDocument;
import com.mvc.enterprises.entities.PageResponseDto;
import com.mvc.enterprises.entities.Product;
import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.form.OrderDocumentForm;
import com.mvc.enterprises.utils.ILConstants;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orderDocument")
public class OrderDocumentController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(OrderQtyController.class);
	
	private final RestTemplate restTemplate;
	
	public OrderDocumentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
	
    //@Autowired
    //private OrderDocumentService orderDocumentService;

    /**
     * Show all all orders from mongo db
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(
    		value = "/showMongoDbDetails",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
    public String showMongoDbDetails(Model model, HttpServletRequest request,
    		RedirectAttributes redirectAttributes) throws Exception {
    	
    	User user = (User) request.getSession().getAttribute(Utils.getSessionLoginUserIdKey());
    	
    	_LOGGER.info(">>> Inside showMongoDbDetails. userId:<<<"+user.getUserId());
    	
		//List<OrderDocument> orderDocuments = orderDocumentService.findByUserId(user.getUserId());
    	
    	String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
    	
    	_LOGGER.info(">>> Inside showMongoDbDetails. url:<<<"+url);
    	
    	HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
    	
    	ResponseEntity<List<OrderDocument>> response = null;
    	
    	try {
    		Map<String, String> uriVariables = new HashMap<>();
	    	if(Utils.getAdminRole().equalsIgnoreCase(user.getRole())) {
	    		url = url+"/allmongousers";
	    		_LOGGER.info(">>> Inside showMongoDbDetails. inside if url:<<<"+url);
	    		response = restTemplate.exchange(
	        	   		url,
	        	        HttpMethod.GET,
	        	        entity,
	        	        new ParameterizedTypeReference<List<OrderDocument>>() {},
	        	        uriVariables
            		);
	    	} else {
	    		url = url+"/mongouserid/{userId}";
	    		_LOGGER.info(">>> Inside showMongoDbDetails. inside else url:<<<"+url);
	    		//Call REST API WITH headers
	    		uriVariables.put("userId", String.valueOf(user.getUserId()));
	    		response = restTemplate.exchange(
	        	   		url,
	        	        HttpMethod.GET,
	        	        entity,
	        	        new ParameterizedTypeReference<List<OrderDocument>>() {},
	        	        uriVariables
            		);
	    	}
		} catch (Exception exp ) {
			_LOGGER.error("Error:"+exp.toString());
			//throw new Exception("Network Authentication Required");
			model.addAttribute("error", "Network Authentication Required.");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			_LOGGER.error("Error: Unauthorized");
			model.addAttribute("error", "Unauthorized.");
			//throw new Exception("Unauthorized");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			_LOGGER.error("Error:Not Found ");
			model.addAttribute("error", "Not Found.");
			//throw new Exception("Not Found");
		}
		
		List<OrderDocument> orderDocuments = response.getBody();
		
		if(!orderDocuments.isEmpty() && orderDocuments.size() > 0) {
			_LOGGER.info("showMongoDbDetails orderDocuments: "+orderDocuments.size());
		} else {
			_LOGGER.info("showMongoDbDetails orderDocuments: 0");
		}
		
		OrderDocumentForm form = new OrderDocumentForm();
		
		try {
			retrieveForSelections(form, request);
	    } catch (Exception exp) {
	    	redirectAttributes.addFlashAttribute("error", exp.toString());
			return "redirect:returnILHome";
		}
		
		form.setOrderDocument(new OrderDocument());
    	
    	if(!orderDocuments.isEmpty() && orderDocuments.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultOrderDocuments(orderDocuments);
    	}
    	
    	model.addAttribute("orderDocumentForm", form);
    	
        return "orderDocument/orderDocumentHome";
    }
    
    /**
     * Submit the OrderDocument search page
     * 
     * @param OrderDocument
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/orderDocumentSearch")
    public String orderDocumentSearch(@ModelAttribute("orderDocumentForm") OrderDocumentForm form, 
    										Model model,
    										HttpServletRequest request) throws Exception {
    	_LOGGER.info("Entered into orderDocumentSearch");
    	
       	Long manufacturerId = form.getOrderDocument().getManufacturerId();
       	Long productId = form.getOrderDocument().getProductId();
       	User user = (User) request.getSession().getAttribute(Utils.getSessionLoginUserIdKey());
       	Long userId = user.getUserId();
       	
       	_LOGGER.info("manufacturerId: "+manufacturerId);
       	_LOGGER.info("productId: "+productId);
       	_LOGGER.info("userId: "+userId);
 
        List<OrderDocument> orderDocuments = null;
        
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
		ResponseEntity<List<OrderDocument>> response = null;
		
		Map<String, String> uriVariables = new HashMap<>();
		//Microservice endpoint
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
        
		try {
			if(manufacturerId != null && manufacturerId > 0) {
				uriVariables.put("manufacturerId", String.valueOf(manufacturerId));
			} else {
				uriVariables.put("manufacturerId", "0");
			}
        	
        	if(productId != null && productId > 0) {
        		uriVariables.put("productId", String.valueOf(productId));
			} else {
				uriVariables.put("productId", "0");
			}
        	
        	
        	if(userId != null && userId > 0) {
        		uriVariables.put("userId", String.valueOf(userId));
			} else {
				uriVariables.put("userId", "0");
			}
        	
        	url = url+"/searchmongo/mfgproductuser?manufacturerId={manufacturerId}&productId={productId}&userId={userId}";
        	_LOGGER.info(">>> Inside orderDocumentSearch. url:<<<"+url);
        	
            //Call microservice
            response = restTemplate.exchange(
	        	   		url,
	        	        HttpMethod.GET,
	        	        entity,
	        	        new ParameterizedTypeReference<List<OrderDocument>>() {},
	        	        uriVariables
            		);
	    } catch (Exception ex) {
	    	model.addAttribute("error", "No order documents are find for selected criteria.");
	    }
        
		orderDocuments = response.getBody();
		
        //OrderDocumentForm form = new OrderDocumentForm();
   	
		//form.setOrderDocument(orderDocument);
    	
    	if(!orderDocuments.isEmpty() && orderDocuments.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultOrderDocuments(orderDocuments);
    	} else {
    		model.addAttribute("error", "No orders are find for selected criteria.");
    	}
    	
    	try {
    		retrieveForSelections(form, request);
    	} catch (Exception exp) {
    		model.addAttribute("error", exp.toString());
    	}
    	
    	model.addAttribute("orderDocumentForm", form);
        
        return "orderDocument/orderDocumentHome";
    }
    
    /**
     * Get manufacturers and products
     * @param form
     */
    
	private void retrieveForSelections(OrderDocumentForm form, HttpServletRequest request) throws Exception {
		try {
			//List<Manufacturer> manufacturers = orderDocumentService.findAllManufacturers();
			List<Manufacturer> manufacturers = getRestAllManufacturers(request.getSession());
			form.setManufacturers(manufacturers);
		} catch (Exception exp) {
			throw exp;
		}
		
		try {
	    	//List<Product> products = orderQtyService.findAllProducts();
			List<Product> products = getRestAllProducts(request.getSession());
	    	form.setProducts(products);
		} catch (Exception exp) {
			throw exp;
		}
	}
    
    /**
     * Return to IL home
     * 
     * @param session
     * @return
     */
    @GetMapping("/returnILHome")
    public String returnILHome(HttpSession session) {
    	User user = (User) session.getAttribute(Utils.getSessionLoginUserIdKey());
        if (user == null) {
            return "forward:/login"; // forward if not logged in
        }
        return "ilHome";
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
	
	/**
	 * Get all manufacturer from RESTFUL
	 * @return
	 */
	private List<Manufacturer> getRestAllManufacturers(HttpSession session) throws Exception {
		
		_LOGGER.info(">>> Inside getRestAllManufacturers. <<<");
		
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL;
		
		//ResponseEntity<Manufacturer[]> response = restTemplate.getForEntity(url, Manufacturer[].class, entity);
		ResponseEntity<PageResponseDto<Manufacturer>> response = null;
		
		try {
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
			
			String urlBuilder = UriComponentsBuilder
			        .fromHttpUrl(url)
			        .queryParam("page", 0)
			        .queryParam("size", 1000)
			        .queryParam("sort", "mfgName")
			        .toUriString();
			
			response =
			    restTemplate.exchange(
			    		urlBuilder,
			            HttpMethod.GET,
			            entity,
			            new ParameterizedTypeReference<PageResponseDto<Manufacturer>>() {}
			    );	
		} catch (Exception exp ) {
			throw new Exception("Network Authentication Required or Manufacturer microservice is not connection.");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			throw new Exception("Unauthorized or Manufacturer microservice is not connection.");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new Exception("Not Found or Manufacturer microservice is not connection.");
		}
		
		return response.getBody().getContent();
	}
	
	/**
	 * Get all manufacturer from RESTFUL
	 * @return
	 */

	private List<Product> getRestAllProducts(HttpSession session) throws Exception {
		_LOGGER.info(">>> Inside getRestAllProducts. <<<");
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL;
		
		ResponseEntity<PageResponseDto<Product>> response = null;
		
		try {
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
			String urlBuilder = UriComponentsBuilder
			        .fromHttpUrl(url)
			        .queryParam("page", 0)
			        .queryParam("size", 1000)
			        .queryParam("sort", "productName")
			        .toUriString();
				
				response =
				    restTemplate.exchange(
				    		urlBuilder,
				            HttpMethod.GET,
				            entity,
				            new ParameterizedTypeReference<PageResponseDto<Product>>() {}
				    );	
		} catch (Exception exp) {
			_LOGGER.info(">>> Inside getRestAllProducts exception. <<< "+exp.toString());
			throw new Exception("Network Authentication Required or Product microservice is not connection.");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			_LOGGER.info(">>> Inside getRestAllProducts. <<< Unauthorized");
			throw new Exception("Unauthorized or Product microservice is not connection.");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			_LOGGER.info(">>> Inside getRestAllProducts. <<< Not Found<<< ");
			throw new Exception("Not Found or Product microservice is not connection.");
		}
		
		return response.getBody().getContent();
	}

}
