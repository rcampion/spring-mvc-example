package com.rkc.zds.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.rkc.zds.dto.LoginDto;
import com.rkc.zds.model.LoginBean;
import com.rkc.zds.service.AuthenticationService;
//import com.rkc.zds.service.SecurityService;

@Controller
public class UserController {

	//@Autowired
	//private SecurityService securityService;

    @Autowired
    private AuthenticationService authenticationService;
    
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showForm(Model model) {
		model.addAttribute("user", new LoginBean());
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@Valid @ModelAttribute(value = "user") final LoginBean user, BindingResult bindingResult, ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (bindingResult.hasErrors()) {
			return "login";
		}
		
		//securityService.autologin(user.getUserName(), user.getPassword());
	   	LoginDto loginDTO = new LoginDto();
	   	
	   	loginDTO.setLogin(user.getUserName());
	   	loginDTO.setPassword(user.getPassword());
	   	
		authenticationService.authenticate(loginDTO, request, response);

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		String requestURI = request.getRequestURI();
		System.out.println("requestURI = " + requestURI);
		
		if(authentication.isAuthenticated()) {
			//return "redirect:/welcome";
			return "welcome";
		}
		return "login";
	}

	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcome(Model model) {
		return "welcome";
	}
}
