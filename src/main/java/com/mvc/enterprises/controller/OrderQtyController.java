
package com.mvc.enterprises.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.transaction.annotation.Transactional;
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
import com.mvc.enterprises.entities.OrderQty;
import com.mvc.enterprises.entities.Product;
import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.form.OrderQtyForm;
import com.mvc.enterprises.utils.ILConstants;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/orderqty")
public class OrderQtyController {
      
    private static final Logger _LOGGER = LoggerFactory.getLogger(OrderQtyController.class);
	
	private final RestTemplate restTemplate;

    public OrderQtyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Show all order qty
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(
    		value = "/showOrderQtyDetails",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
    public String showOrderQtyDetails(Model model, HttpServletRequest request) throws Exception {
    	
    	_LOGGER.info(">>> Inside showOrderQtyDetails. 1 <<<");
    	
    	OrderQtyForm form = getAllOrderQtys(request);
    	
    	_LOGGER.info(">>> Inside showOrderQtyDetails. 2 <<<");
    	
    	retrieveForSelections(form, request);
    	
    	model.addAttribute("orderQtyForm", form);
    	
        return "orderqty/orderQtyHome";
    }
    
    /**
     * Submit the Order Qty search page
     * 
     * @param Manufacturer
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/orderQtySearch")
    public String orderQtySearch(@ModelAttribute("orderqty") OrderQty orderQty, 
    										Model model,
    										HttpServletRequest request) throws Exception  {
    	
    	_LOGGER.info(">>> Inside orderQtySearch. <<<");
    	
       	Long manufacturerId = orderQty.getManufacturer().getManufacturerId();
       	Long productId = orderQty.getProduct().getProductId();
       	User user = (User) request.getSession().getAttribute(Utils.getSessionLoginUserIdKey());
       	Long userId = user.getUserId();
       	
       	_LOGGER.info(">>> Inside orderQtySearch. manufacturerId:<<<"+manufacturerId);
       	_LOGGER.info(">>> Inside orderQtySearch. productId:<<<"+productId);
       	_LOGGER.info(">>> Inside orderQtySearch. userId:<<<"+userId);
       	
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
		ResponseEntity<List<OrderQty>> response = null;
 
        List<OrderQty> orderQtys = null;
        
        boolean exceptionThrow = false;
        
		Map<String, String> uriVariables = new HashMap<>();
		//Microservice endpoint
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
        
        try {
	        if (manufacturerId != null && manufacturerId.longValue() > 0 && productId != null && productId.longValue() > 0) {
	        	//orderQtys = orderQtyService.findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(manufacturerId, productId, userId);
	        	//url = url+"/search/mfgproductuser?manufacturerId={"+manufacturerId+"}&productId={"+productId+"}&userId={"+userId+"}";
	            
	            uriVariables.put("manufacturerId", String.valueOf(manufacturerId));
	        	uriVariables.put("productId", String.valueOf(productId));
	        	uriVariables.put("userId", String.valueOf(userId));
	        	
	        	url = url+"/search/mfgproductuser?manufacturerId={manufacturerId}&productId={productId}&userId={userId}";
	        	_LOGGER.info(">>> Inside orderQtySearch. url:<<<"+url);
	        	
	            //Call microservice
	            response = restTemplate.exchange(
		        	   		url,
		        	        HttpMethod.GET,
		        	        entity,
		        	        new ParameterizedTypeReference<List<OrderQty>>() {},
		        	        uriVariables
	            		);
	        } else if (manufacturerId != null && manufacturerId.longValue() > 0) {
	        	//orderQtys = orderQtyService.findByManufacturer_ManufacturerIdAndUser_UserId(manufacturerId, userId);
	        	//url = url+"/search/mfguser?manufacturerId={"+manufacturerId+"}&userId={"+userId+"}";
	        	url = url+"/search/mfguser?manufacturerId={manufacturerId}&userId={userId}";
	            _LOGGER.info(">>> Inside orderQtySearch. url:<<<"+url);
	            
	            uriVariables.put("manufacturerId", String.valueOf(manufacturerId));
	        	uriVariables.put("userId", String.valueOf(userId));
	            
	        	//Call microservice
	            response = restTemplate.exchange(
		        	   		url,
		        	        HttpMethod.GET,
		        	        entity,
		        	        new ParameterizedTypeReference<List<OrderQty>>() {},
		        	        uriVariables
	            		);
	        } else if (productId != null && productId.longValue() > 0) {
	        	//orderQtys = orderQtyService.findByProduct_ProductIdAndUser_UserId(productId, userId);
	        	//url = url+"/search/productuser?productId={"+productId+"}&userId={"+userId+"}";
	        	url = url+"/search/productuser?productId={productId}&userId={userId}";
	            _LOGGER.info(">>> Inside orderQtySearch. url:<<<"+url);
	            
	            uriVariables.put("productId", String.valueOf(productId));
	        	uriVariables.put("userId", String.valueOf(userId));
	            
	        	//Call microservice
	            response = restTemplate.exchange(
		        	   		url,
		        	        HttpMethod.GET,
		        	        entity,
		        	        new ParameterizedTypeReference<List<OrderQty>>() {},
		        	        uriVariables
	            		);
	        } else {
	        	//orderQtys = orderQtyService.findByUser_UserId(userId);
	        	// Build URL with query parameters
	            url = url+"/userid/{userId}";
	            _LOGGER.info(">>> Inside orderQtySearch. url:<<<"+url);
	            
	            uriVariables.put("userId", String.valueOf(userId));
	            
	            //Call microservice
	            response = restTemplate.exchange(
		        	   		url,
		        	        HttpMethod.GET,
		        	        entity,
		        	        new ParameterizedTypeReference<List<OrderQty>>() {},
		        	        uriVariables
	            		);
	        }
	    } catch (Exception ex) {
	    	exceptionThrow = true;
	    	model.addAttribute("error", "No orders are find for selected criteria.");
	    }
        
        
        // Convert array to list
        if(response != null) {
        	orderQtys = response.getBody();
        } else {
        	if(!exceptionThrow) {
        		orderQtys = getRestAllOrderQtys(request.getSession());
        	}
        }
        
        OrderQtyForm form = new OrderQtyForm();
		
		form.setOrderQty(orderQty);
    	
    	if(orderQtys != null && !orderQtys.isEmpty() && orderQtys.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultOrderQtys(orderQtys);
    	} else {
    		model.addAttribute("error", "No orders are find for selected criteria.");
    	}
    	
    	retrieveForSelections(form, request);
    	
    	//Reset the selected values
    	form.setManufacturer(orderQty.getManufacturer());
    	form.setProduct(orderQty.getProduct());
    	
    	model.addAttribute("orderQtyForm", form);
        
        return "orderqty/orderQtyHome";
    }
    
    /**
     * Add new order qty
     * 
     * @param user
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayNewOrderQty")
    public String displayNewOrderQty(Model model, HttpServletRequest request) throws Exception {
    	OrderQty orderQty = new OrderQty();

    	model.addAttribute("orderQty", orderQty);
		
    	OrderQtyForm form = new OrderQtyForm();
    	
    	retrieveForSelections(form, request);
    	
    	model.addAttribute("orderQtyForm", form);
    	
        return "orderqty/addOrderQty";
    }
    
    /**
     * Save OrderQty
     * 
     * @param product
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/saveNewOrderQty")
    @Transactional
    public String saveNewOrderQty(@ModelAttribute("orderqty") OrderQty orderQty, 
							Model model,
							HttpServletRequest request) throws Exception {
    	
    	_LOGGER.info(">>> Inside saveNewOrderQty.<<<");
    	
    	OrderQtyForm form = new OrderQtyForm();
    	form.setOrderQty(orderQty);
    	
    	List<String> errors = checkInput(orderQty);
    	
    	boolean processValidation = false;
    	
       	if (errors.isEmpty()) {
       		
       		String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
       		
       		User user = (User) request.getSession().getAttribute(Utils.getSessionLoginUserIdKey());
       		
    		Map<String, String> uriVariables = new HashMap<>();
    		//Microservice endpoint
    		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
       		
       		//Find out already order qty exists
       		//List<OrderQty> orderQtys = orderQtyService.findByManufacturer_ManufacturerIdAndProduct_ProductIdAndUser_UserId(
       		//		orderQty.getManufacturer().getManufacturerId(), 
       		//		orderQty.getProduct().getProductId(), 
       		//		user.getUserId());
       		//Call microservice
       		uriVariables.put("manufacturerId", String.valueOf(orderQty.getManufacturer().getManufacturerId()));
        	uriVariables.put("productId", String.valueOf(orderQty.getProduct().getProductId()));
        	uriVariables.put("userId", String.valueOf(user.getUserId()));
        	
        	url = url+"/search/mfgproductuser?manufacturerId={manufacturerId}&productId={productId}&userId={userId}";
        	_LOGGER.info(">>> Inside saveNewOrderQty. url:<<<"+url);
    		
    		//Call REST API
	        ResponseEntity<List<OrderQty>> response = 
	        		restTemplate.exchange(
	        				url, 
			        		HttpMethod.GET,
		        	        entity,
		        	        new ParameterizedTypeReference<List<OrderQty>>() {},
		        	        uriVariables
	        			);
    		
            List<OrderQty> orderQtys = response.getBody();
            
            _LOGGER.info(">>> saveNewOrderQty-->orderQtys.<<<: "+orderQtys);
       		
       		if(orderQtys != null && orderQtys.size() > 0) {
       			errors.add("Order already existing in the system..");
       		} else {
       			
       			_LOGGER.info(">>> saveNewOrderQty-->else.<<<: ");
       		
	       		orderQty.setUser(user);
	       		
	       		//Get manufacturer details for input mango db.
	       		//Manufacturer manufacturer = orderQtyService.findByManufacturerId(orderQty.getManufacturer().getManufacturerId());
	       		//Get manufacturer micro service
	       		//Microservice endpoint
				Manufacturer manufacturer = getRestManufacturerById(orderQty.getManufacturer().getManufacturerId(), request);
	            //Get manufacturer
	       		orderQty.setManufacturer(manufacturer);
	       	       		
	       		//Get product details from input mango db.
	       		//Product product = orderQtyService.findByProductId(orderQty.getProduct().getProductId());
	       		//Get product micro service
	       		//Microservice endpoint
	            Product product = getRestProductById(orderQty.getProduct().getProductId(), request);
	            //Get product
	       		orderQty.setProduct(product);
	       				
	       		//OrderQty result = orderQtyService.saveOrUpdate(orderQty);
	       		//Microservice endpoint
	            // Call microservice POST endpoint
	       		url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
	       		HttpEntity<OrderQty> httpOrderQtyEntity = getJwtTokenToHttpRequest(request.getSession(), orderQty);
	       		
	       		//Call microservice POST endpoint
	       		ResponseEntity<OrderQty> responseOrderQty = restTemplate.exchange(
	            	        url,
	            	        HttpMethod.POST,
	            	        httpOrderQtyEntity,
	            	        OrderQty.class
	            	    );
	       		
	       		OrderQty result = responseOrderQty.getBody();
		    	
		    	if(result != null && result.getOrderId() > 0) {
			    	
			    	model.addAttribute("orderQtyForm", form);
			    	
			    	model.addAttribute("msg", "Order added successfully.");
			    	
			    	processValidation = true;
			    	
			    	//Execute kafka mongodb and file
			    	//try {
			    		//Send the value to kafka producer
			    		//result.setDocumentType(ILConstants.MONGODB_OPERATION_SAVE);
			    		//_LOGGER.info(">>> saveNewOrderQty-->before sending kafka producer.<<<: "+ILConstants.MONGODB_OPERATION_SAVE);
						//kafkaProducerService.send(result);
			    	//} catch(Exception ex) {
			       		//DONT throw any exception
			    	//}
			    	
			    	//User is save sent to user home
			    	return "forward:/orderqty/showOrderQtyDetails"; 
		    	} else {
		    		model.addAttribute("error", "Order not added into the system.");
		    	}
       		}
    	} 
       	
       	if(!processValidation) {
    		retrieveForSelections(form, request);
    	   	//Reset the selected values
        	form.setManufacturer(orderQty.getManufacturer());
        	form.setProduct(orderQty.getProduct());
        	
        	form.getOrderQty().setQuantity(orderQty.getQuantity());
        	
           	model.addAttribute("error", errors);
    	}
       	model.addAttribute("orderQtyForm", form);
    	//If error display same page
        return "orderqty/addOrderQty";
    }    
    /**
     * display update order qty
     * 
     * @param product id
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayUpdateOrderQty")
    public String displayUpdateOrderQty(@RequestParam("orderId") Long orderId, 
    									Model model,
    									HttpServletRequest request) {
    	
    	_LOGGER.info(">>> Inside displayUpdateOrderQty.<<<");
    	_LOGGER.info(">>> orderId display = <<<"+ orderId);
    	
    	//OrderQty orderQty = orderQtyService.findByOrderId(orderId);
    	OrderQty orderQty = getRestOrderQtyByOrderId(orderId, request);
    	
    	model.addAttribute("orderqty", orderQty);
    	
    	OrderQtyForm form = new OrderQtyForm();
		
		form.setOrderQty(orderQty);
		
		model.addAttribute("orderQtyForm", form);
    	
        return "orderqty/updateOrderQty";
    }
    
    /**
     * Update order qty
     * 
     * @param orderQty
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/updateOrderQty")
    @Transactional
    public String updateOrderQty(@ModelAttribute("orderQty") OrderQty orderQty, 
							Model model,
							HttpServletRequest request) throws Exception {
    	
    	_LOGGER.info(">>> Inside updateOrderQty.<<<");
    	_LOGGER.info(">>> getOrderId ID =  <<<"+ orderQty.getOrderId());
    	
        OrderQtyForm form = new OrderQtyForm();
        form.setOrderQty(orderQty);
        
       	model.addAttribute("orderQtyForm", form);
   		
       	//OrderQty existingOrderQty = orderQtyService.findByOrderId(orderQty.getOrderId());
       	OrderQty existingOrderQty = getRestOrderQtyByOrderId(orderQty.getOrderId(), request);
   		
   		if(existingOrderQty != null && existingOrderQty.getOrderId() > 0) {
   			
   			existingOrderQty.setQuantity(orderQty.getQuantity());
   			
   			User user = (User) request.getSession().getAttribute(Utils.getSessionLoginUserIdKey());
   			existingOrderQty.setUser(user);
   			
       		//Get manufacturer details for input mango db.
       		//Manufacturer manufacturer = orderQtyService.findByManufacturerId(existingOrderQty.getManufacturer().getManufacturerId());
   			Manufacturer manufacturer = getRestManufacturerById(existingOrderQty.getManufacturer().getManufacturerId(), request);
       		existingOrderQty.setManufacturer(manufacturer);
       	       		
       		//Get product details from input mango db.
       		//Product product = orderQtyService.findByProductId(existingOrderQty.getProduct().getProductId());
       		Product product = getRestProductById(existingOrderQty.getProduct().getProductId(), request);
       		existingOrderQty.setProduct(product);
   		
       		HttpEntity<OrderQty> httpEntityPost = getJwtTokenToHttpRequest(request.getSession(), existingOrderQty);
       		
   			//OrderQty result = orderQtyService.updateOrderQty(existingOrderQty);
       		//OrderQty result = restTemplate.postForObject(ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL, existingOrderQty, OrderQty.class);
       		
       		//Microservice endpoint
   			String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
            // Call microservice POST endpoint
   			ResponseEntity<OrderQty> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntityPost,
                    OrderQty.class,
                    existingOrderQty
                );
		
   			OrderQty result = response.getBody();
	    	
	    	if(result != null && result.getOrderId() > 0) {
		    	
		    	model.addAttribute("orderQtyForm", form);
		    	
		    	model.addAttribute("msg", "Order updated successfully.");
		    	
		    	//Execute kafka mongodb and file
		    	//try {
		    		//Send the value to kafka producer
		    		//result.setDocumentType(ILConstants.MONGODB_OPERATION_UPDATE);
		    		//_LOGGER.info(">>> updateOrderQty-->before sending kafka producer.<<<: "+ILConstants.MONGODB_OPERATION_UPDATE);
					//kafkaProducerService.send(result);
		    	//} catch(Exception ex) {
		       		//DONT throw any exception
		    	//}
		    	
		    	//User is save sent to user home
		    	return "forward:/orderqty/showOrderQtyDetails"; 
	    	} else {
	    		model.addAttribute("error", "Order not updated into the system.");
	    	}
   		} else {
   			model.addAttribute("error", "Order not existed into the system.");
   		}
 
    	//If error display same page
        return "orderqty/updateOrderQty";
    }
    
    /**
     * display delete order qty
     * 
     * @param productId
     * @param model
     * @return
     */
    @GetMapping("/displayDeleteOrderQty")
    public String displayDeleteProduct(@RequestParam("orderId") Long orderId, 
    									Model model,
    									HttpServletRequest request) {
    	_LOGGER.info(">>> Inside updateOrderQty.<<<");
    	_LOGGER.info(">>> Order ID display =.<<<");
    	
    	//OrderQty orderQty = orderQtyService.findByOrderId(orderId);
    	OrderQty orderQty = getRestOrderQtyByOrderId(orderId, request);
    	
    	model.addAttribute("orderqty", orderQty);
    	
    	OrderQtyForm form = new OrderQtyForm();
		
    	form.setOrderQty(orderQty);
    		
    	model.addAttribute("orderQtyForm", form);
    	
        return "orderqty/deleteOrderQty";
    }
    
    /**
     * Delete order qty
     * 
     * @param orderQty
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/deleteOrderQty")
    @Transactional
    public String deleteOrderQty(@ModelAttribute("orderQty") OrderQty orderQty, 
							Model model,
							HttpServletRequest request) {

    	System.out.println("getOrderId ID = " + orderQty.getOrderId());
    	
        OrderQtyForm form = new OrderQtyForm();
        
        form.setOrderQty(orderQty);
         		
        //OrderQty existingOrderQty = orderQtyService.findByOrderId(orderQty.getOrderId());
        OrderQty existingOrderQty = getRestOrderQtyByOrderId(orderQty.getOrderId(), request);
   		
   		if(existingOrderQty != null && existingOrderQty.getOrderId() > 0) {
   			
   			//orderQtyService.deleteByOrderId(existingOrderQty.getOrderId());
      		//Microservice endpoint
   			String msg = null;
   			String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL+"/{id}";
   			try {
   				
   				HttpEntity<Product> entity = getJwtTokenToHttpRequest(request.getSession(), null);
   				
   				//Call microservice DELETE endpoint
   				restTemplate.exchange(
   				            url,
   				            HttpMethod.DELETE,
   				            entity,
   				            Void.class,
   				            existingOrderQty.getOrderId()   // URI variable
   				        );   				
	            
	   			msg = "Order deleted successfully.";
	   			model.addAttribute("msg", msg);
	   			
	   			//Execute kafka mongodb and file
		    	//try {
		    		//Send the value to kafka producer
		    		//existingOrderQty.setDocumentType(ILConstants.MONGODB_OPERATION_DELETE);
		    		//_LOGGER.info(">>> deleteOrderQty-->before sending kafka producer.<<<: "+ILConstants.MONGODB_OPERATION_DELETE);
					//kafkaProducerService.send(existingOrderQty);
		    	//} catch(Exception ex) {
		       		//DONT throw any exception
		    	//}
	   			
   			} catch (HttpClientErrorException.NotFound e) {
   	            model.addAttribute("error", "Order not found with id: " + existingOrderQty.getOrderId());
   	        } catch (HttpServerErrorException e) {
   	            model.addAttribute("error", "Server error occurred: " + e.getResponseBodyAsString());
   	        } catch (Exception e) {
   	            model.addAttribute("error", "Unexpected error: " + e.getMessage());
   	        }
   			
	    	form = getAllOrderQtys(request);
	    	
	    	model.addAttribute("orderQtyForm", form);
	    	
	    	//model.addAttribute("msg", "Product deleted successfully.");
	    	if(msg != null && !msg.isEmpty()) {
		    	//User is save sent to user home
	    		return "forward:/orderqty/showOrderQtyDetails"; 
	    	}
   		} else {
   			model.addAttribute("error", "Order not existed into the system.");
   		}
    	
    	//If error display same page
        return "orderqty/deleteOrderQty";
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
     * Get manufacturers and products
     * @param form
     */
    
	private void retrieveForSelections(OrderQtyForm form, HttpServletRequest request) throws Exception {
		//List<Manufacturer> manufacturers = orderQtyService.findAllManufacturers();
		List<Manufacturer> manufacturers = getRestAllManufacturers(request.getSession());
    	//List<Product> products = orderQtyService.findAllProducts();
		List<Product> products = getRestAllProducts(request.getSession());
    	
    	form.setManufacturers(manufacturers);
    	form.setProducts(products);
	}
    
    /**
     * Retrieving all orderQtys. This method is used in retrieve and save.
     */
	private OrderQtyForm getAllOrderQtys(HttpServletRequest request) {
		
		User user = (User) request.getSession().getAttribute(Utils.getSessionLoginUserIdKey());
		
		_LOGGER.info("user.getUserId()"+user.getUserId());
		
		//List<OrderQty> orderQtys = orderQtyService.findByUser_UserId(user.getUserId());
		List<OrderQty> orderQtys = null;
		
		Map<String, String> uriVariables = new HashMap<>();
		//Microservice endpoint
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
		
		String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL+"/userid/{userId}";
        _LOGGER.info(">>> Inside getAllOrderQtys. url:<<<"+url);
        
        uriVariables.put("userId", String.valueOf(user.getUserId()));
        
        //Call microservice
        ResponseEntity<List<OrderQty>> response = restTemplate.exchange(
        	   		url,
        	        HttpMethod.GET,
        	        entity,
        	        new ParameterizedTypeReference<List<OrderQty>>() {},
        	        uriVariables
        		);
        
        orderQtys = response.getBody();
		
		if(orderQtys != null && orderQtys.size() > 0) {
			_LOGGER.info("orderQtys.size()"+orderQtys.size());
		}
    		
		OrderQtyForm form = new OrderQtyForm();
		
		form.setOrderQty(new OrderQty());
    	
    	if(!orderQtys.isEmpty() && orderQtys.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultOrderQtys(orderQtys);
    	}
    	_LOGGER.info(">>> Inside getAllOrderQtys. END <<<");
		return form;
	}
	
	/**
	 * 
	 * @param manufacturerId
	 * @return
	 */
	private Manufacturer getRestManufacturerById(Long manufacturerId, HttpServletRequest request) {
		_LOGGER.info(">>> Inside getRestManufacturerById. <<<");
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_MANUFACTURER_URL+"/id/{manufacturerId}";
		_LOGGER.info(">>> Inside getRestManufacturerById. <<<"+url);
        //Call REST API
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
	 * 
	 * @param productId
	 * @return
	 */
	private Product getRestProductById(Long productId, HttpServletRequest request) {
		_LOGGER.info(">>> Inside getRestProductById. <<<");
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
		String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL+"/id/{productId}";
		//Call REST API
		ResponseEntity<Product> response =
	                restTemplate.exchange(
	                    url,
	                    HttpMethod.GET,
	                    entity,                 // <-- JWT included
	                    Product.class,
	                    productId          // <-- path variable
	                );
	    //Get manufacturer
		return response.getBody();
	}
	
	/**
	 * Get product by id.
	 * 
	 * @param productId
	 * @return
	 */
	private OrderQty getRestOrderQtyByOrderId(Long orderId, HttpServletRequest request) {
		_LOGGER.info(">>> Inside getRestProductById. <<<");
		//Microservice endpoint
        HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
		String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL+"/orderid/{orderId}";
		//Call REST API
		ResponseEntity<OrderQty> response =
	                restTemplate.exchange(
	                    url,
	                    HttpMethod.GET,
	                    entity,                 // <-- JWT included
	                    OrderQty.class,
	                    orderId          // <-- path variable
	                );
	    //Get manufacturer
		return response.getBody();
	}
	
	/**
	 * Get all manufacturer from RESTFUL
	 * @return
	 */
	private List<OrderQty> getRestAllOrderQtys(HttpSession session) throws Exception {
		_LOGGER.info(">>> Inside getRestAllOrderQtys. <<<");
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_ORDERQTY_URL;
		
		ResponseEntity<OrderQty[]> response = null;
		
		try {
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
			_LOGGER.info(">>> Inside getRestAllOrderQtys. before calling microservice.<<<");
			response =
		        restTemplate.exchange(
		                url,
		                HttpMethod.GET,
		                entity,
		                OrderQty[].class
		        );	
			_LOGGER.info(">>> Inside getRestAllOrderQtys. after calling microservice.<<<");
		} catch (Exception exp) {
			_LOGGER.info(">>> Inside getRestAllOrderQtys exception. <<< "+exp.toString());
			throw new Exception("Network Authentication Required");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			_LOGGER.info(">>> Inside getRestAllOrderQtys. <<< Unauthorized");
			throw new Exception("Unauthorized");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			_LOGGER.info(">>> Inside getRestAllOrderQtys. <<< Not Found<<< ");
			throw new Exception("Not Found");
		}
		
		return Arrays.asList(response.getBody());
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
	 * Get all manufacturer from RESTFUL
	 * @return
	 */

	private List<Product> getRestAllProducts(HttpSession session) throws Exception {
		_LOGGER.info(">>> Inside getRestAllProducts. <<<");
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL;
		
		ResponseEntity<Product[]> response = null;
		
		try {
			HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
			_LOGGER.info(">>> Inside getRestAllProducts. before calling microservice.<<<");
			response =
		        restTemplate.exchange(
		                url,
		                HttpMethod.GET,
		                entity,
		                Product[].class
		        );	
			_LOGGER.info(">>> Inside getRestAllProducts. after calling microservice.<<<");
		} catch (Exception exp) {
			_LOGGER.info(">>> Inside getRestAllProducts exception. <<< "+exp.toString());
			throw new Exception("Network Authentication Required");
		}
		
		if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			_LOGGER.info(">>> Inside getRestAllProducts. <<< Unauthorized");
			throw new Exception("Unauthorized");
		} else if(response.getStatusCode() == HttpStatus.NOT_FOUND) {
			_LOGGER.info(">>> Inside getRestAllProducts. <<< Not Found<<< ");
			throw new Exception("Not Found");
		}
		
		return Arrays.asList(response.getBody());
	}
	
	/**
	 * Check inputs conditions
	 * 
	 * @param order qty
	 * @return
	 */
	private List<String> checkInput(OrderQty orderQty) {
		List<String> errors = new ArrayList<>();
    	
    	//Check the conditions
    	if(orderQty.getManufacturer().getManufacturerId() == null || 
    			(orderQty.getManufacturer().getManufacturerId() != null && orderQty.getManufacturer().getManufacturerId() == 0)) {
    		errors.add("Select manufacturer name.");
    	}
    	
    	//Check the conditions
    	if(orderQty.getProduct().getProductId() == null || 
    			(orderQty.getProduct().getProductId() != null && orderQty.getProduct().getProductId() == 0)) {
    		errors.add("Select product name.");
    	}
    	
    	//Check the conditions
    	if(orderQty.getQuantity() == null || (orderQty.getQuantity() != null && orderQty.getQuantity() == 0)) {
    		errors.add("Enter valid quantity.");
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
