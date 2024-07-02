package com.demo.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.demo.entity.User;
import com.demo.repository.UserRepository;
import com.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@ModelAttribute
	public void commonUser(Principal p,Model m)
	{
		if(p!=null){
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user",user);
		}
	}
	
	@GetMapping("/")
	public String index()
	{
		return "index";
	}
	
	@GetMapping("/register")
	public String register()
	{
		return "register";
	}
	
	@GetMapping("/signin")
	public String userlogin()
	{
		return "login";
	}
	
	@GetMapping("/about")
	public String about()
	{
		return "about";
	}
	
	@PostMapping("/createUser")
	public String saveUser(@ModelAttribute User user,HttpSession session)
	{
		
		System.out.println(user);
		User u = userService.saveuser(user);
		
		if(u!=null) {
			//System.out.println("Save Success");
			session.setAttribute("msg", "Register Successfully");
		}else {
			//System.out.println("Error in Server");
			session.setAttribute("msg", "Something Wrong on Server");
		}
		
		return "redirect:/register";
	}
}
