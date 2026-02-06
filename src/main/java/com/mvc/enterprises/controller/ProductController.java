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

import com.mvc.enterprises.entities.Product;
import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.form.ProductForm;
import com.mvc.enterprises.utils.ILConstants;
import com.mvc.enterprises.utils.StringUtility;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {
	
	private static final Logger _LOGGER = LoggerFactory.getLogger(ProductController.class);
    //@Autowired
    //private ProductService productService;
	
	private final RestTemplate restTemplate;

    public ProductController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Show all products
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping(
    		value = "/showProductDetails",
    		method = {RequestMethod.GET, RequestMethod.POST}
    )
    public String showProductDetails(Model model, HttpServletRequest request) throws Exception {
    	_LOGGER.info(">>> Inside showProductDetails. <<<");
    	ProductForm form = getAllProducts(request);
    	
    	model.addAttribute("productForm", form);
    	
        return "product/productHome";
    }
    
    /**
     * Submit the product search page
     * 
     * @param Manufacturer
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/productSearch")
    public String productSearch(@ModelAttribute("product") Product product, 
    										Model model,
    										HttpSession session) throws Exception {
    	
    	_LOGGER.info(">>> Inside showProductDetails. <<<");
     	
       	String productName = product.getProductName();
       	String productDescription = product.getProductDescription();
       	String casNumber = product.getCasNumber();
       	
       	_LOGGER.info(">>> Inside showProductDetails. productName:<<<"+productName);
       	_LOGGER.info(">>> Inside showProductDetails. productDescription:<<<"+productDescription);
       	_LOGGER.info(">>> Inside showProductDetails. casNumber:<<<"+casNumber);
 
        List<Product> products = null;
        
		//Microservice endpoint
		String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL;
		
		ResponseEntity<List<Product>> response = null;	
		
		boolean exceptionThrow = false;
		
		Map<String, String> uriVariables = new HashMap<>();
		//Microservice endpoint
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(session, null);
        
		try {
	        if (!StringUtility.isEmpty(productName) && !StringUtility.isEmpty(productDescription) && !StringUtility.isEmpty(casNumber)) {    	
	        	//products = productService.findByProductNameDesCanNumber(productName.trim(), productDescription.trim(), casNumber.trim());
	        	
	        	uriVariables.put("productName", productName.trim());
	        	uriVariables.put("productDescription", productDescription.trim());
	        	uriVariables.put("casNumber", casNumber.trim());
	        	
	        	// Build URL with query parameters
	            //url = url+"/search?name={"+productName+"}&description={"+productDescription+"}&casNumber={"+casNumber+"}";
	            url = url+"/search?name={productName}&description={productDescription}&casNumber={casNumber}";
	            _LOGGER.info(">>> Inside showProductDetails. url:<<<"+url);
	            //Call microservice
	            response = restTemplate.exchange(
		        	   		url,
		        	        HttpMethod.GET,
		        	        entity,
		        	        new ParameterizedTypeReference<List<Product>>() {},
		        	        uriVariables
	            		);

	        } else if (!StringUtility.isEmpty(productName)) {
	        	//products = productService.findByProductNameLike(productName);
	           	// Build URL with query parameters
	            //url = url+"/search/productName/{"+productName+"}";
	        	uriVariables.put("productName", productName.trim());
	        	url = url+"/search/productName/{productName}";
	            _LOGGER.info(">>> Inside showProductDetails. url:<<<"+url);
	            //Call microservice
	            response = restTemplate.exchange(
			    	   		url,
			    	        HttpMethod.GET,
			    	        entity,
			    	        new ParameterizedTypeReference<List<Product>>() {},
			    	        uriVariables
	            		);
	        } else if (!StringUtility.isEmpty(productDescription)) {
	        	//products = productService.findByProductDescriptionLike(productDescription);
	           	// Build URL with query parameters
	            //url = url+"/search/description/{"+productDescription+"}";
	        	uriVariables.put("productDescription", productDescription.trim());
	        	url = url+"/search/description/{productDescription}";
	            _LOGGER.info(">>> Inside showProductDetails. url:<<<"+url);
	            //Call microservice
	            response = restTemplate.exchange(
		    	   		url,
		    	        HttpMethod.GET,
		    	        entity,
		    	        new ParameterizedTypeReference<List<Product>>() {},
		    	        uriVariables
            		);
	        } else if (!StringUtility.isEmpty(casNumber)) {
	        	//products = productService.findByCasNumberLike(casNumber);
	           	// Build URL with query parameters
	            //url = url+"/search/cas/{"+casNumber+"}";
	        	uriVariables.put("casNumber", casNumber.trim());
	        	url = url+"/search/cas/{casNumber}";
	            _LOGGER.info(">>> Inside showProductDetails. url:<<<"+url);
	            //Call microservice
	            response = restTemplate.exchange(
		    	   		url,
		    	        HttpMethod.GET,
		    	        entity,
		    	        new ParameterizedTypeReference<List<Product>>() {},
		    	        uriVariables
            		);
	        }
	    } catch (Exception ex) {
	    	exceptionThrow = true;
	    	model.addAttribute("error", "No products are find for selected criteria.");
	    }
        
        // Convert array to list
        if(response != null) {
        	products = response.getBody();
        } else {
        	if(!exceptionThrow) {
        		products = getRestAllProducts(session);
        	}
        }
        
		ProductForm form = new ProductForm();
		
		form.setProduct(product);
    	
    	if(products != null && !products.isEmpty() && products.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultProducts(products);
    	} else {
    		model.addAttribute("error", "No products are find for selected criteria.");
    	}
    	
    	model.addAttribute("productForm", form);
        
        return "product/productHome";
    }
    
    /**
     * Add new product
     * 
     * @param user
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayNewProduct")
    public String displayNewManufacturer(Model model, HttpServletRequest request) {
    	
    	_LOGGER.info(">>> Inside displayNewManufacturer. <<<");
    	
    	Product product = new Product();

    	model.addAttribute("product", product);
		
    	ProductForm form = new ProductForm();
    	
    	model.addAttribute("productForm", form);
    	
        return "product/addProduct";
    }
    
    /**
     * Save Product
     * 
     * @param product
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/saveNewProduct") 
    public String saveNewProduct(@ModelAttribute("product") Product product, 
							Model model,
							HttpServletRequest request) throws Exception {
    	
    	_LOGGER.info(">>> Inside saveNewProduct. <<<");
    	
    	ProductForm form = new ProductForm();
    	form.setProduct(product);
    	
    	List<String> errors = checkInput(product);
    	
       	//Check product name already existing system.
    	//Product productExisting = productService.findByProductName(product.getProductName());
    	Product productExisting = null;
    	//Get the token http request
    	HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
    	try {
    		Map<String, String> uriVariables = new HashMap<>();
        	uriVariables.put("name", product.getProductName().trim());
	    	//Microservice endpoint
	        String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL+"/name/{name}";
	        //Call REST API
	        ResponseEntity<Product> response = 
	        		restTemplate.exchange(
	        				url, 
			        		HttpMethod.GET,
		        	        entity,
		        	        Product.class,
		        	        uriVariables
	        			);
	        // Get product object
	        productExisting = response.getBody();
    	} catch (Exception ex) {
    		//Dont throw the exception. Because it valid exception.
    	}
    	if(productExisting != null && productExisting.getProductId() > 0) {
    		errors.add("Product name already existing in the system.");
    	}
       	
       	model.addAttribute("productForm", form);
       	model.addAttribute("error", errors);
    	
       	if (errors.isEmpty()) {
       		HttpEntity<Product> httpProductEntity = getJwtTokenToHttpRequest(request.getSession(), product);
       		//Product result = productService.saveOrUpdate(product);
       		//Microservice endpoint
       		String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL;
            // Call microservice POST endpoint
            ResponseEntity<Product> response =
            	    restTemplate.exchange(
            	        url,
            	        HttpMethod.POST,
            	        httpProductEntity,
            	        Product.class
            	    );
            
            Product result = response.getBody();
	    	
	    	if(result != null && result.getProductId() > 0) {
	    	
		    	form = getAllProducts(request);
		    	
		    	model.addAttribute("productForm", form);
		    	
		    	model.addAttribute("msg", "Product added successfully.");
		    	
		    	//User is save sent to user home
		    	return "forward:/product/showProductDetails"; 
	    	} else {
	    		model.addAttribute("error", "Product not added into the system.");
	    	}
    	}
    	//If error display same page
        return "product/addProduct";
    }
    
    /**
     * display update product
     * 
     * @param product id
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/displayUpdateProduct")
    public String displayUpdateProduct(@RequestParam("productId") Long productId, 
    									Model model,
    									HttpServletRequest request) {
    	
    	_LOGGER.info(">>> Inside displayUpdateProduct. <<<");
    	
    	_LOGGER.info(">>> Product ID display. <<<"+ productId);
    	
    	//Product product = productService.findByProductId(productId);
    	//Micro service call
    	Product product = getRestProductByProductId(productId, request);
    	
    	model.addAttribute("product", product);
    	
    	ProductForm form = new ProductForm();
    	
    	product.setProductId(productId);
    	
    	form.setProductId(productId);
		
		form.setProduct(product);
		
    	model.addAttribute("productForm", form);
    	
        return "product/updateProduct";
    }
    
    /**
     * Update product
     * 
     * @param product
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/updateProduct")
    @Transactional
    public String updateProduct(@ModelAttribute("product") Product product, 
							Model model,
							HttpServletRequest request) throws Exception {
    	
    	_LOGGER.info(">>> Inside displayUpdateProduct. <<<");
    	
    	_LOGGER.info("Product ID = " + product.getProductId());
    	
        ProductForm form = new ProductForm();
        form.setProduct(product);
        
    	//Check the conditions
    	List<String> errors = checkInput(product);
    		
       	model.addAttribute("productForm", form);
       	
       	model.addAttribute("error", errors);
    	
       	if (errors.isEmpty()) {
       		
       		//Get the token http request
    		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
       		
       		String modifiedProductName = product.getProductName();
       		Product dbOldProduct = getRestProductByProductId(product.getProductId(), request);
       		
       		_LOGGER.info("modifiedProductName = " + modifiedProductName);
       		_LOGGER.info("dbOldProduct.getProductName() = " + dbOldProduct.getProductName());
       		
       		//boolean isProductExisting = false;
       		Product productNameExists = null;
       		Map<String, String> uriVariables = new HashMap<>();
       		ResponseEntity<Product> response = null;
       		if(!modifiedProductName.equalsIgnoreCase(dbOldProduct.getProductName())) {
	       		//Check modified product name existing in the system
	        	//Product productNameExists = productService.findByProductName(product.getProductName());	       		
	       		
	           	//Microservice endpoint
	       		try {
	       			uriVariables.put("productName", product.getProductName().trim());
	       			String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL+"/name/{productName}";
		            _LOGGER.info(">>> Inside updateProduct. url:<<<"+url);
		            //Call microservice
		            response = restTemplate.exchange(
				    	   		url,
				    	        HttpMethod.GET,
				    	        entity,
				    	        Product.class,
				    	        uriVariables
		            		);
		            
		            //Get product
		            productNameExists = response.getBody();
		            
	       		} catch (Exception ex) {
	       			_LOGGER.info("Product name already existing in the system."+modifiedProductName);
	       		}
       		}
        	
        	if(productNameExists != null && productNameExists.getProductId() > 0) {        	
        		errors.add("Updated product name already existing in the system.");       	
        	} else {
       		
	       		//Product existingProduct = productService.findByProductId(product.getProductId());
        		Product existingProduct = getRestProductByProductId(product.getProductId(), request);
	       		
	       		if(existingProduct != null && existingProduct.getProductId() > 0) {
	       			
	       			existingProduct.setProductName(product.getProductName());
	       			existingProduct.setProductDescription(product.getProductDescription());
	       			existingProduct.setCasNumber(product.getCasNumber());
	       			
	       			HttpEntity<Product> httpEntityPost = getJwtTokenToHttpRequest(request.getSession(), existingProduct);
	       		
	       			//Product result = productService.saveOrUpdate(existingProduct);
	          		//Microservice endpoint
	       			String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL;
	                // Call microservice POST endpoint
	           		response = restTemplate.exchange(
	                        url,
	                        HttpMethod.POST,
	                        httpEntityPost,
	                        Product.class,
	                        existingProduct
	                    );
       		
	           		Product result = response.getBody();
       		
			    	if(result != null && result.getProductId() > 0) {
			    	
				    	form = getAllProducts(request);
				    	
				    	model.addAttribute("productForm", form);
				    	
				    	model.addAttribute("msg", "Product updated successfully.");
				    	
				    	//User is save sent to user home
				    	return "forward:/product/showProductDetails"; 
			    	} else {
			    		model.addAttribute("error", "Product not updated into the system.");
			    	}
	       		} else {
	       			model.addAttribute("error", "Product not existed into the system.");
	       		}
        	}
    	}
    	//If error display same page
        return "product/updateProduct";
    }
    
    /**
     * display delete product
     * 
     * @param productId
     * @param model
     * @return
     */
    @GetMapping("/displayDeleteProduct")
    public String displayDeleteProduct(@RequestParam("productId") Long productId, 
    									Model model,
    									HttpServletRequest request) {
    	_LOGGER.info(">>> Inside displayDeleteProduct. <<<");
     	_LOGGER.info(">>> Inside displayDeleteProduct. <<<:"+"productId: "+productId);
    	
    	//Product product = productService.findByProductId(productId);
     	//Microserive call
     	Product product = getRestProductByProductId(productId, request);
    	
    	model.addAttribute("product", product);
    	
    	ProductForm form = new ProductForm();
    	
    	product.setProductId(productId);
    	
    	form.setProductId(productId);
		
		form.setProduct(product);
		
    	model.addAttribute("productForm", form);
    	
        return "product/deleteProduct";
    }
    
    /**
     * Delete product
     * 
     * @param product
     * @param model
     * @param request
     * @return
     */
    @PostMapping("/deleteProduct")
    @Transactional
    public String deleteProduct(@ModelAttribute("product") Product product, 
							Model model,
							HttpServletRequest request) throws Exception {
    	
    	_LOGGER.info(">>> Inside deleteProduct. <<<");
     	_LOGGER.info(">>> Inside deleteProduct. <<<:"+"Product ID = "+product.getProductId());

     	_LOGGER.info(">>> Product ID = <<<:" + product.getProductId());
     	_LOGGER.info(">>> getProductName = <<<:" + product.getProductName());
     	_LOGGER.info(">>> getProductDescription = <<<:" + product.getProductDescription());
     	_LOGGER.info(">>> getCasNumber = <<<:" + product.getCasNumber());
    	
        ProductForm form = new ProductForm();
        form.setProduct(product);
        model.addAttribute("productForm", form);
         		
        //Product existingProduct = productService.findByProductId(product.getProductId());
    	//Micro service call
        Product existingProduct = getRestProductByProductId(product.getProductId(), request);
        
        _LOGGER.info(">>> Inside deleteProduct. existingProduct <<<"+existingProduct.getProductId());
   		
   		if(existingProduct != null && existingProduct.getProductId() > 0) {
   			
   			_LOGGER.info(">>> Inside existingProduct <<<");
   			
   			form.setProduct(existingProduct);
   	        model.addAttribute("productForm", form);
   		
   			//productService.deleteByProductId(product.getProductId());
      		//Microservice endpoint
   			String msg = null;
   			String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL+"/{id}";
   			try {
	            //Call microservice DELETE endpoint
	   			//restTemplate.delete(url, product.getProductId());
   				//Get the token http request
   				HttpEntity<Product> entity = getJwtTokenToHttpRequest(request.getSession(), null);
   	    		
	            //Call microservice DELETE endpoint
   				restTemplate.exchange(
   				            url,
   				            HttpMethod.DELETE,
   				            entity,
   				            Void.class,
   				            product.getProductId()   // URI variable
   				        );
   				
	   			msg = "Product deleted successfully.";
	   			model.addAttribute("msg", msg);
	   			
	   			_LOGGER.info("Product deleted successfully.");
	   			
   			} catch (HttpClientErrorException.NotFound e) {
   	            model.addAttribute("error", "Product not found with id: " + product.getProductId());
   	        } catch (HttpServerErrorException e) {
   	            model.addAttribute("error", "Server error occurred: " + e.getResponseBodyAsString());
   	        } catch (Exception e) {
   	            model.addAttribute("error", "Unexpected error: " + e.getMessage());
   	        }
   			
	    	
	    	if(msg != null && !msg.isEmpty()) {
	    		form = getAllProducts(request);
				model.addAttribute("msg", "Product deleted successfully.");
				model.addAttribute("productForm", form);
				
		    	//User is save sent to user home
	    		return "forward:/product/showProductDetails";  
	    	}
   		} else {
   			model.addAttribute("error", "Product not existed into the system.");
   		}
    	
    	//If error display same page
        return "product/deleteProduct";
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
	 * Get product by id.
	 * 
	 * @param productId
	 * @return
	 */
	private Product getRestProductByProductId(Long productId, HttpServletRequest request) {
		_LOGGER.info(">>> Inside getRestProductByProductId. <<<");
		HttpEntity<Void> entity = getJwtTokenToHttpRequest(request.getSession(), null);
		//Microservice endpoint
        String url = ILConstants.MICROSERVICE_RESTFUL_PRODUCT_URL+"/id/{productId}";
        //Call REST API
        //Call REST API WITH headers
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
     * Retrieving all products. This method is used in retrieve and save.
     */
	private ProductForm getAllProducts(HttpServletRequest request) throws Exception {
		
		List<Product> products = getRestAllProducts(request.getSession());
    	
		ProductForm form = new ProductForm();
		form.setProduct(new Product());
    	
    	if(!products.isEmpty() && products.size() > 0) {
    		form.setShowDetails(true);
    		form.setResultProducts(products);
    	}
		return form;
	}
	
	/**
	 * Check inputs conditions
	 * 
	 * @param manufacturer
	 * @return
	 */
	
	private List<String> checkInput(Product product) {
		List<String> errors = new ArrayList<>();
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(product.getProductName())) {
    		errors.add("Enter valid product name.");
    	}
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(product.getProductDescription())) {
    		errors.add("Enter valid product description.");
    	}
    	
    	//Check the conditions
    	if(StringUtility.isEmpty(product.getCasNumber())) {
    		errors.add("Enter valid cas number.");
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
