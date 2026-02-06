package com.mvc.enterprises.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.mvc.enterprises.entities.Manufacturer;
import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.form.ManufacturerForm;
import com.mvc.enterprises.utils.ILConstants;
import com.mvc.enterprises.utils.StringUtility;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manufacturer")
public class ManufacturerController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(ManufacturerController.class);
	
	private final RestTemplate restTemplate;

    public ManufacturerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
	
    //@Autowired
    //private ManufacturerService manufacturerService;

    /**
     * Show all Manufacturers
     * 
     * @param model
     * @param request
     * @return
     */
    //@GetMapping("/showManufacturerDetails")
    @RequestMapping(
    		value = "/showManufacturerDetails",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
    public String showManufacturerDetails(Model model, HttpServletRequest request)  {
    	
    	_LOGGER.info(">>> Inside showManufacturerDetails. <<<");
    	
    	ManufacturerForm form = null;
    	try {
    		form = getAllManufacturers(request.getSession());
    	} catch (Exception exp) {
    		form = new ManufacturerForm();
    		model.addAttribute("error", exp.getMessage());
    		return "redirect:returnILHome";
    	}
    	
    	model.addAttribute("manufacturerForm", form);
    	
        return "manufacturer/manufacturerHome";
    }
    
    /**
     * Submit the Manufacturer search page
     * 
     * @param Manufacturer
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/manufacturerSearch")
    public String manufacturerSearch(@ModelAttribute("manufacturer") Manufacturer manufacturer, 
    										Model model,
    										HttpSession session) {
    	
    	_LOGGER.info(">>> Inside manufacturerSearch. <<<");
       	String manufacturerName = manufacturer.getMfgName();
       	
       	_LOGGER.info(">>> Inside manufacturerSearch. <<<:"+"manufacturerName: "+manufacturerName);
 
        List<Manufacturer> manufacturers = null;
        
        if (!StringUtility.isEmpty(manufacturerName)) {
        	//manufacturers = manufacturerService.findByManufacturerNameLike(manufacturerName.trim());
        	//Microservice endpoint
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
        	
        	Map<String, String> uriVariables = new HashMap<>();
        	uriVariables.put("mfgName", manufacturerName.trim());
            
        	String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL+"/search/{mfgName}";

            //Call REST API
            //ResponseEntity<Manufacturer[]> response = restTemplate.getForEntity(url, Manufacturer[].class, entity, manufacturerName.trim());
        	ResponseEntity<Manufacturer[]> response = restTemplate.exchange(
        	        url,
        	        HttpMethod.GET,
        	        entity,
        	        Manufacturer[].class,
        	        uriVariables
        	);	
        	

            // Convert array to list
            manufacturers = Arrays.asList(response.getBody());
        } else {
        	//manufacturers = manufacturerService.findAllManufacturers();
        	
    		//Call restful web service
    		//public ResponseEntity<List<Manufacturer>> getAll() {
        	try {
        		manufacturers = getRestAllManufacturers(session);
        	} catch (Exception exp) {
        		model.addAttribute("error", exp.getMessage());
        	}
        }
        
		ManufacturerForm form = new ManufacturerForm();
		
		form.setManufacturer(manufacturer);
    	
    	if(manufacturers != null && !manufacturers.isEmpty() && manufacturers.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultManufacturers(manufacturers);
    	} else {
    		model.addAttribute("error", "No manufacturers are find for selected criteria.");
    	}
    	
    	model.addAttribute("manufacturerForm", form);
        
        return "manufacturer/manufacturerHome";
    }
    
    /**
     * Add new manufacturer
     * 
     * @param user
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayNewManufacturer")
    public String displayNewManufacturer(Model model, HttpServletRequest request) {
    	_LOGGER.info(">>> Inside displayNewManufacturer. <<<");
    	
    	Manufacturer manufacturer = new Manufacturer();

    	model.addAttribute("manufacturer", manufacturer);
		
    	ManufacturerForm form = new ManufacturerForm();
    	
    	model.addAttribute("manufacturerForm", form);
    	
        return "manufacturer/addManufacturer";
    }
    
    /**
     * Save manufacturer
     * 
     * @param user
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/saveNewManufacturer") 
    public String saveNewManufacturer(@ModelAttribute Manufacturer manufacturer, 
							Model model,
							HttpServletRequest request) {
    	
    	_LOGGER.info(">>> Inside saveNewManufacturer. <<<");
    	
    	ManufacturerForm form = new ManufacturerForm();
    	form.setManufacturer(manufacturer);
    	
    	List<String> errors = checkInput(manufacturer);
    	
       	//Check mfg name already existing system.
    	//Manufacturer manufacturerExisting = manufacturerService.findByMfgName(manufacturer.getMfgName());
    	Manufacturer manufacturerExisting = null;
    	//Get the token http request
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
    	try {
    		Map<String, String> uriVariables = new HashMap<>();
        	uriVariables.put("mfgName", manufacturer.getMfgName().trim());
        	
	    	//Microservice endpoint
	        String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL+"/name/{mfgName}";
	        //Call REST API
	        ResponseEntity<Manufacturer> response = 
	        		restTemplate.exchange(
	        				url, 
			        		HttpMethod.GET,
		        	        entity,
		        	        Manufacturer.class,
		        	        uriVariables
	        			);
	        // Get manufacturer object
	        manufacturerExisting = response.getBody();
	        
    	} catch (Exception ex) {
    		//Dont throw the exception. Because it valid exception.
    	}
    	
    	_LOGGER.info(">>> manufacturer not existing <<<");
    	
    	if(manufacturerExisting != null && manufacturerExisting.getManufacturerId() > 0) {
    		errors.add("Manufacturer name already existing in the system.");
    	}
       	
       	model.addAttribute("manufacturerForm", form);
       	model.addAttribute("error", errors);
    	
       	if (errors.isEmpty()) {    
       		HttpEntity<Manufacturer> httpMfgEntity = getJwtTokenToHttpRequest(request.getSession(), manufacturer);
       		//Manufacturer result = manufacturerService.saveOrUpdate(manufacturer);    		
       		//Microservice endpoint
       		String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL;
            // Call microservice POST endpoint
            ResponseEntity<Manufacturer> response =
            	    restTemplate.exchange(
            	        url,
            	        HttpMethod.POST,
            	        httpMfgEntity,
            	        Manufacturer.class
            	    );
            
            Manufacturer result = response.getBody();
	    	
	    	if(result != null && result.getManufacturerId() > 0) {
	    		
	    		try {
	    			form = getAllManufacturers(request.getSession());
	    			model.addAttribute("msg", "Manufacturer added successfully.");
	    		} catch (Exception exp) {
	    			model.addAttribute("error", exp.getMessage());
	    		}
		    	
		    	model.addAttribute("manufacturerForm", form);

		    	//User is save sent to user home
		    	return "forward:/manufacturer/showManufacturerDetails"; 
	    	} else {
	    		model.addAttribute("error", "Manufacturer not added into the system.");
	    	}
    	}
    	//If error display same page
        return "manufacturer/addManufacturer";
    }
    
    /**
     * display update manufacturer
     * 
     * @param user
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayUpdateManufacturer")
    public String displayUpdateManufacturer(@RequestParam("manufacturerId") Long manufacturerId, 
    										Model model,
    										HttpServletRequest request) {
    	
    	_LOGGER.info(">>> Inside displayUpdateManufacturer. <<<");
    	_LOGGER.info(">>> Inside displayUpdateManufacturer. <<<:"+"manufacturerId: "+manufacturerId);
    	
    	//Manufacturer manufacturer = manufacturerService.findByManufacturerId(manufacturerId);
    	
    	//Micro service call
    	Manufacturer manufacturer = getRestManufacturerByManufacturerId(manufacturerId, request);
    	
    	model.addAttribute("manufacturer", manufacturer);
    	
    	ManufacturerForm form = new ManufacturerForm();
    	
    	manufacturer.setManufacturerId(manufacturerId);
    	
    	form.setManufacturerId(manufacturerId);
		
		form.setManufacturer(manufacturer);
		
    	model.addAttribute("manufacturerForm", form);
    	
        return "manufacturer/updateManufacturer";
    }
    
    /**
     * Update manufacturer
     * 
     * @param manufacturer
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/updateManufacturer")
    public String updateManufacturer(@ModelAttribute("manufacturer") Manufacturer manufacturer, 
							Model model,
							HttpServletRequest request) {

        _LOGGER.info(">>> Inside updateManufacturer. <<<");
    	_LOGGER.info(">>> Inside updateManufacturer. <<<:"+"manufacturerId: "+manufacturer.getManufacturerId());
    	
        ManufacturerForm form = new ManufacturerForm();
        form.setManufacturer(manufacturer);
        
    	//Check the conditions
    	List<String> errors = checkInput(manufacturer);
       	
       	model.addAttribute("manufacturerForm", form);
       	
       	model.addAttribute("error", errors);
    	
       	if (errors.isEmpty()) {
       		
       		//Manufacturer existingMfg = manufacturerService.findByManufacturerId(manufacturer.getManufacturerId());
       		
       		//Get the token http request
    		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
  	
        	//Microservice endpoint
            String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL+"/id/{id}";
            //Call REST API
            ResponseEntity<Manufacturer> response = 
            		restTemplate.exchange(
            				url, 
            				 HttpMethod.GET,
                             entity,                 // <-- JWT included
                             Manufacturer.class,
                             manufacturer.getManufacturerId()          // <-- path variable  				
            			);
            //Get manufacturer
            Manufacturer existingMfg = response.getBody();
       		
       		if(existingMfg != null && existingMfg.getManufacturerId() > 0) {
       			
       			_LOGGER.info(">>> Inside updateManufacturer-->existingMfg. <<<");
       			
       			existingMfg.setMfgName(manufacturer.getMfgName());
       			existingMfg.setAddress1(manufacturer.getAddress1());
       			existingMfg.setAddress2(manufacturer.getAddress2());
       			existingMfg.setCity(manufacturer.getCity());
       			existingMfg.setState(manufacturer.getState());
       			existingMfg.setZip(manufacturer.getZip());
       			existingMfg.setZipExt(manufacturer.getZipExt());
       			
       			HttpEntity<Manufacturer> httpEntityPost = getJwtTokenToHttpRequest(request.getSession(), existingMfg);
       		
       			//Manufacturer result = manufacturerService.saveOrUpdate(existingMfg);
           		//Microservice endpoint
           		url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL;
                // Call microservice POST endpoint
           		response = restTemplate.exchange(
		                        url,
		                        HttpMethod.POST,
		                        httpEntityPost,
		                        Manufacturer.class,
		                        existingMfg
		                    );
           		
           		Manufacturer result = response.getBody();
		    	
		    	if(result != null && result.getManufacturerId() > 0) {
		    		try {
		    			form = getAllManufacturers(request.getSession());
		    			model.addAttribute("msg", "Manufacturer updated successfully.");
		    		} catch (Exception exp) {
		    			model.addAttribute("error", exp.getMessage());
		    		}
			    	
			    	model.addAttribute("manufacturerForm", form);
			    	
			    	//User is save sent to user home
			    	return "forward:/manufacturer/showManufacturerDetails"; 
		    	} else {
		    		model.addAttribute("error", "Manufacturer not updated into the system.");
		    	}
       		} else {
       			model.addAttribute("error", "Manufacturer not existed into the system.");
       		}
    	}
    	//If error display same page
        return "manufacturer/updateManufacturer";
    }
    
    /**
     * display delete manufacturer
     * 
     * @param manufacturer
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayDeleteManufacturer")
    public String displayDeleteManufacturer(@RequestParam("manufacturerId") Long manufacturerId, 
    										Model model,
    										HttpServletRequest request) {
    	
        _LOGGER.info(">>> Inside displayDeleteManufacturer. <<<");
     	_LOGGER.info(">>> Inside displayDeleteManufacturer. <<<:"+"manufacturerId: "+manufacturerId);
    	
    	//Manufacturer manufacturer = manufacturerService.findByManufacturerId(manufacturerId);
     	
     	//Microserive call
       	Manufacturer manufacturer = getRestManufacturerByManufacturerId(manufacturerId, request);
    	
    	model.addAttribute("manufacturer", manufacturer);
    	
    	ManufacturerForm form = new ManufacturerForm();
    	
    	manufacturer.setManufacturerId(manufacturerId);
    	
    	form.setManufacturerId(manufacturerId);
		
		form.setManufacturer(manufacturer);
		
    	model.addAttribute("manufacturerForm", form);
    	
        return "manufacturer/deleteManufacturer";
    }


    
    /**
     * Delete manufacturer
     * 
     * @param manufacturer
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/deleteManufacturer")
    public String deleteManufacturer(@ModelAttribute("manufacturer") Manufacturer manufacturer, 
							Model model,
							HttpServletRequest request) {

    	_LOGGER.info(">>> Inside deleteManufacturer. <<<");
     	_LOGGER.info(">>> Inside deleteManufacturer. <<<:"+"manufacturerId: "+manufacturer.getManufacturerId());
    	
        ManufacturerForm form = new ManufacturerForm();
        form.setManufacturer(manufacturer);
        model.addAttribute("manufacturerForm", form);
        
         		
        //Manufacturer existingMfg = manufacturerService.findByManufacturerId(manufacturer.getManufacturerId());
        
    	//Micro service call
    	Manufacturer existingMfg = getRestManufacturerByManufacturerId(manufacturer.getManufacturerId(), request);
   		
   		if(existingMfg != null && existingMfg.getManufacturerId() > 0) {
   		
   			//manufacturerService.deleteByManufacturerId(manufacturer.getManufacturerId());
   			form.setManufacturer(existingMfg);
   	        model.addAttribute("manufacturerForm", form);
   			
       		//Microservice endpoint
   			String msg = null;
   			String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL+"/{id}";
   			try {
   				//Get the token http request
   				HttpEntity<Manufacturer> entity = getJwtTokenToHttpRequest(request.getSession(), null);
   	    		
	            //Call microservice DELETE endpoint
   				restTemplate.exchange(
   				            url,
   				            HttpMethod.DELETE,
   				            entity,
   				            Void.class,
   				            manufacturer.getManufacturerId()   // URI variable
   				        );
	       		//Manufacturer result = manufacturerService.saveOrUpdate(manufacturer);  
	   			msg = "Manufacturer deleted successfully.";
	   			model.addAttribute("msg", msg);
   			} catch (HttpClientErrorException.NotFound e) {
   	            model.addAttribute("error", "Manufacturer not found with id: " + manufacturer.getManufacturerId());
   	        } catch (HttpServerErrorException e) {
   	            model.addAttribute("error", "Server error occurred: " + e.getResponseBodyAsString());
   	        } catch (Exception e) {
   	            model.addAttribute("error", "Unexpected error: " + e.getMessage());
   	        }
	    	
	    	if(msg != null && !msg.isEmpty()) {
	    		try {
	   				form = getAllManufacturers(request.getSession());
	   				model.addAttribute("msg", "Manufacturer deleted successfully.");
	   				model.addAttribute("manufacturerForm", form);
			    	//User is save sent to user home
			    	return "forward:/manufacturer/showManufacturerDetails"; 
	   			} catch (Exception exp) {
	    			model.addAttribute("error", exp.getMessage());
	    		}
	    	}
   		} else {
   			model.addAttribute("error", "Manufacturer not existed into the system.");
   		}
    	
    	//If error display same page
        return "manufacturer/deleteManufacturer";
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
     * Retrieving all Manufacturers. This method is used in retrieve and save.
     */
	private ManufacturerForm getAllManufacturers(HttpSession session) throws Exception {
		
		_LOGGER.info(">>> Inside getAllManufacturers. <<<");
		
		//Below method is for monolithic approach
		//List<Manufacturer> manufacturers = manufacturerService.findAllManufacturers();
		
		//Call restful web service
		//public ResponseEntity<List<Manufacturer>> getAll() {
		List<Manufacturer> manufacturers = getRestAllManufacturers(session);
    		
		ManufacturerForm form = new ManufacturerForm();
		
		form.setManufacturer(new Manufacturer());
    	
    	if(!manufacturers.isEmpty() && manufacturers.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultManufacturers(manufacturers);
    	}
		return form;
	}
	
	/**
	 * Get manufacturer by id.
	 * 
	 * @param manufacturerId
	 * @return
	 */
	private Manufacturer getRestManufacturerByManufacturerId(Long manufacturerId, HttpServletRequest request) {
		
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
		
		//Microservice endpoint
        String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL+"/id/{id}";
        
        //Call REST API WITH headers
        ResponseEntity<Manufacturer> response =
                restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,                 // <-- JWT included
                    Manufacturer.class,
                    manufacturerId          // <-- path variable
                );
        //Get manufacturer
		return response.getBody();
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
		ResponseEntity<Manufacturer[]> response = null;
		
		try {
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
			response =
		        restTemplate.exchange(
		                url,
		                HttpMethod.GET,
		                entity,
		                Manufacturer[].class
		        );	
		} catch (Exception exp ) {
			throw new Exception("Network Authentication Required");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			throw new Exception("Unauthorized");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new Exception("Not Found");
		}
		
		return Arrays.asList(response.getBody());
	}
	
	/**
	 * Check inputs conditions
	 * 
	 * @param manufacturer
	 * @return
	 */
	
	private List<String> checkInput(Manufacturer manufacturer) {
		List<String> errors = new ArrayList<>();
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(manufacturer.getMfgName())) {
    		errors.add("Enter valid manufacturer name.");
    	}
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(manufacturer.getAddress1())) {
    		errors.add("Enter valid address1.");
    	}
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(manufacturer.getCity())) {
    		errors.add("Enter valid city.");
    	}
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(manufacturer.getState())) {
    		errors.add("Enter valid state.");
    	}
    	
      	//Check the conditions
    	if(StringUtility.isEmpty(manufacturer.getZip())) {
    		errors.add("Enter valid zip.");
    	}
		return errors;
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
