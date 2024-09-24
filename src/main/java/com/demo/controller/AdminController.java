package com.demo.controller;

import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.dto.PasswordForm;
import com.demo.entity.Bus;
import com.demo.entity.Conductor;
import com.demo.entity.Driver;
import com.demo.entity.Ticket;
import com.demo.entity.User;
import com.demo.repository.BusRepository;
import com.demo.repository.ConductorRepository;
import com.demo.repository.DriverRepository;
import com.demo.repository.TicketRepository;
import com.demo.repository.UserRepository;
import com.demo.service.BusService;

import com.demo.service.TicketService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	
    @Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ConductorRepository conductorRepo;
	
	@Autowired
	private DriverRepository driverRepo;
	
	@Autowired
	private BusService busService;
	
	@Autowired
	private TicketService ticketService;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@ModelAttribute
	public void commonUser(Principal p,Model m)
	{
		if(p!=null){
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("admin",user);
		}
	}
	
	@GetMapping("/profile")
	public String profile()
	{
		return "admin_home";
	}
	
	@GetMapping("/addBus")
	public String addBus(Model model)
	{
		List<Driver> driverList = driverRepo.findAll();
        List<Conductor> conductorList = conductorRepo.findAll();
       
        model.addAttribute("driverList", driverList);
        model.addAttribute("conductorList", conductorList);
		return "add_bus";
	}
	
	
	@PostMapping("/createBus")
	public String createBus(@ModelAttribute Bus bus, 
	                        @RequestParam String availableDays, 
	                        @RequestParam(required = false) List<String> specificDays, 
	                        HttpSession session) {
	    String departureTimeStr = bus.getDepartureTime();

	    try {
	        // Create a DateTimeFormatter for 12-hour format with AM/PM
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

	        // Parse the input string into a LocalTime object
	        LocalTime departureTime = LocalTime.parse(departureTimeStr, formatter);
	        
	        // Set the parsed LocalTime into the Bus object
	        bus.setDepartureTime(departureTime);
	        
	        // Set availability based on the selected options
	        if ("Every Day".equals(availableDays)) {
	            bus.setAvailableEveryDay(true);
	            bus.setSpecificDays(null); // Clear specific days if available every day
	        } else {
	            bus.setAvailableEveryDay(false);
	            bus.setSpecificDays(specificDays); // Set specific days
	        }
	        
	        // Save the bus object
	        busService.savebus(bus);
	        
	        session.setAttribute("msg", "Bus added successfully");
	    } catch (DateTimeParseException e) {
	        session.setAttribute("msg", "Invalid time format. Please use hh:mm AM/PM.");
	        return "redirect:/admin/addBus"; 
	    } catch (Exception e) {
	        session.setAttribute("msg", "An error occurred while adding the bus. Please try again.");
	        return "redirect:/admin/addBus";
	    }

	    return "redirect:/admin/addBus";
	}

	@GetMapping("/viewBus")
	public String viewBus(Model m)
	{
		List<Bus> allBus = busService.getAllBus();
		allBus.forEach(x -> System.out.println(x.getSpecificDays()));
		
		m.addAttribute("busList",allBus);
		return "view_allBus";
	}
	
	@GetMapping("/addDriver")
	public String addDriver()
	{
		return "add_driver";
	}
	
	@GetMapping("/addConductor")
	public String addConductor()
	{
		return "add_conductor";
	}
	
	
	@PostMapping("/saveDriver")
	public String saveDriver(@ModelAttribute Driver driver,HttpSession session)
	{
		
		System.out.println(driver);
		
		  Driver d = driverRepo.save(driver);
		  
		  if(d!=null) { 
		  session.setAttribute("msg", "Driver Added Successfully"); 
		  }else {
		   session.setAttribute("msg","Something Wrong on Server"); 
		  }
		 
		
		return "redirect:/admin/addDriver";
	}
	
	
	@PostMapping("/saveConductor")
	public String saveConductor(@ModelAttribute Conductor conductor,HttpSession session)
	{
		
		System.out.println(conductor);
		Conductor c = conductorRepo.save(conductor);
		
		if(c!=null) {
			//System.out.println("Save Success");
			session.setAttribute("msg", "Conductor Added Successfully");
		}else {
			//System.out.println("Error in Server");
			session.setAttribute("msg", "Something Wrong on Server");
		}
		
		return "redirect:/admin/addConductor";
	}
	
	@GetMapping("/tickets")
    public String viewAllTickets(Model model) {
        List<Ticket> tickets = ticketRepository.findAll();
        model.addAttribute("tickets", tickets);
        return "admin_tickets";
    }
	
	@GetMapping("/drivers")
    public String viewAllDriver(Model model) {
        List<Driver> driver = driverRepo.findAll();
        model.addAttribute("drivers", driver);
        return "all_driver";
    }
	
	@GetMapping("/conductors")
    public String viewAllConductors(Model model) {
        List<Conductor> conductors = conductorRepo.findAll();
        model.addAttribute("conductors", conductors);
        return "all_conductors";
    }
	
	@GetMapping("/changePassword")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordForm());
        return "change_password_admin"; // Assuming you have a separate folder for admin-related views
    }

    @PostMapping("/changePassword")
    public String changePassword(@ModelAttribute("passwordForm") PasswordForm passwordForm,
                                 Principal principal,
                                 HttpSession session) {
        if (principal == null) {
            session.setAttribute("error", "Admin not authenticated");
            return "redirect:/admin/login"; // Redirect to admin login page
        }

        String email = principal.getName();
        User admin = userRepo.findByEmail(email);

        // Validate old password
        if (!passwordEncoder.matches(passwordForm.getOldPassword(), admin.getPassword())) {
            session.setAttribute("error", "Incorrect old password");
            return "redirect:/admin/changePassword";
        }

        // Update password
        admin.setPassword(passwordEncoder.encode(passwordForm.getNewPassword()));
        userRepo.save(admin);

        session.setAttribute("msg", "Password changed successfully");

        return "redirect:/admin/admin_home"; // Redirect to admin profile page
    }
    
    
    @GetMapping("/updateBus/{id}")
    public String showUpdateBusForm(@PathVariable("id") int id, Model model) {
        Bus bus = busService.getBusById(id);
        List<Driver> driverList = driverRepo.findAll();
        List<Conductor> conductorList = conductorRepo.findAll();
        model.addAttribute("bus", bus);
        model.addAttribute("driverList", driverList);
        model.addAttribute("conductorList", conductorList);
        return "update_bus";
    }

    @PostMapping("/updateBus")
    public String updateBus(@ModelAttribute Bus bus, HttpSession session) {
        busService.savebus(bus); // Update bus details
        session.setAttribute("msg", "Bus updated successfully");
        return "redirect:/admin/viewBus";
    }

    @GetMapping("/deleteBus/{id}")
    public String deleteBus(@PathVariable("id") int id, HttpSession session) {
        busService.deleteBusById(id); // Delete bus by ID
        session.setAttribute("msg", "Bus deleted successfully");
        return "redirect:/admin/viewBus";
    }
    
    
    @GetMapping("/deleteDriver/{id}")
    public String deleteDriver(@PathVariable("id") int id, HttpSession session) {
        driverRepo.deleteById(id);
        session.setAttribute("msg", "Driver deleted successfully");
        return "redirect:/admin/drivers";
    }
    
    
    @GetMapping("/deleteConductor/{id}")
    public String deleteConductor(@PathVariable("id") int id, HttpSession session) {
        conductorRepo.deleteById(id);
        session.setAttribute("msg", "Conductor deleted successfully");
        return "redirect:/admin/conductors";
    }

}
