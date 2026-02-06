package com.mvc.enterprises.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import com.mvc.enterprises.entities.KeyValuePair;
import com.mvc.enterprises.entities.User;
import com.mvc.enterprises.service.KeyValuePairService;
import com.mvc.enterprises.utils.ILConstants;
import com.mvc.enterprises.utils.JwtUtil;
import com.mvc.enterprises.utils.StringUtility;
import com.mvc.enterprises.utils.Utils;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(LoginController.class);
    
    //@Autowired
    //private UserService userService;
    
    private final RestTemplate restTemplate;

    @Autowired
    private KeyValuePairService keyValuePairService;
    
    public LoginController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * Get login page
     * 
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
    	model.addAttribute("user", new User());
        return "loginHome";
    }

    /**
     * Submit the login page
     * 
     * @param user
     * @param model
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String validateLoginCredentials(@ModelAttribute("user") User user, 
    										Model model,
    										HttpSession session) {
    	
    	//StopWatch stopWatch = new StopWatch();
    	//stopWatch.start();
    	
    	String userName = user.getUserName();
    	String password = user.getPassword();
    	
    	_LOGGER.info("Username: " + userName);
    	_LOGGER.info("Password: " + password);
        
        if(StringUtility.isEmpty(userName) && StringUtility.isEmpty(password)) {
        	model.addAttribute("error", "Enter valid User Id and Password.");
        } else if(StringUtility.isEmpty(userName)) {
    		model.addAttribute("error", "Enter valid User Id.");
    	} else if(StringUtility.isEmpty(password)) {
    		model.addAttribute("error", "Enter valid Password.");
    	} else {
    		
    		String methodMapping = "/validate/user";
    		
    		String url = ILConstants.MICROSERVICE_RESTFUL_USER_URL+methodMapping+"?username={"+userName+"}&password={"+password+"}";
    		
	        //User authenticateUser = userService.findByUserNameAndPassword(userName, password);
            _LOGGER.info(">>> Inside validateLoginCredentials. url:<<<"+url);
            //Call microservice
           
            //Call REST API
            ResponseEntity<User> response = restTemplate.getForEntity(
	    		   url, 
	    		   User.class, 
	    		   userName.trim(),
	    		   password.trim());
            
            User authenticateUser = response.getBody();
	        
	        if(authenticateUser != null && authenticateUser.getUserId() > 0) {
        		session.setAttribute(Utils.getSessionLoginUserIdKey(), authenticateUser);
        		//Create token
        		//Get jwt token
        		List<String> roles = new ArrayList<>();
        		roles.add(Utils.getUserRole());
        		roles.add(Utils.getViewRole());
        		roles.add(Utils.getAdminRole());
        		
        		String token = JwtUtil.generateToken(userName, roles);
        		session.setAttribute(Utils.getTokenKey(),token);
        		
        		_LOGGER.info(">>> user.getUserId() > token <<<"+token);
        		
	            //Get the keyvalue pait from service KeyValuePairService
                Optional<KeyValuePair> result =keyValuePairService.getByKey("kanna");
                _LOGGER.info("KeyValuePair fetched in LoginController: {}", 
                		result.isPresent() ? result.get().toString() : "Not Found");  
                
                //Create authenticated user
                //List<GrantedAuthority> authorities =
                //        List.of(new SimpleGrantedAuthority(roles.toString()));
                
                List<GrantedAuthority> authorities = roles.stream()
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                        .toList();

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                        		userName,
                                null,
                                authorities
                        );

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                
                //stopWatch.stop();
                
                //_LOGGER.info(">>> validateLoginCredentials: process time: <<< "+stopWatch.getTotalTimeMillis() +" ms");
                        
                return "redirect:ilHome";
        	} else {
        		model.addAttribute("error", "User not exists in the system. Enter valid User Id and Password.");
        	}
    	}
        return "loginHome"; // go back to login page
    }

    /**
     * Display IL home page.
     * 
     * @param session
     * @return
     */
    @GetMapping("/ilHome")
    public String ilHomePage(HttpSession session) {
    	User user = (User) session.getAttribute(Utils.getSessionLoginUserIdKey());
        if (user == null) {
            return "redirect:/login"; // redirect if not logged in
        }
        return "ilHome";
    }
    
    /**
     * Display the logout
     * 
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // remove all session attributes
        return "redirect:/login"; // go back to login page
    }
    
    /**
     * Reset the form 
     * 
     * @param session
     * @param model
     * @return
     */
    @GetMapping("/login/reset")
    public String resetLoginForm(HttpSession session, Model model) {
    	session.invalidate(); // remove all session attributes
    	return "redirect:/login"; // go back to login page
    }
}
