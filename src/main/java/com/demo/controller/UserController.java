package com.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.entity.Bus;
import com.demo.entity.Ticket;
import com.demo.entity.User;
import com.demo.repository.TicketRepository;
import com.demo.repository.UserRepository;
import com.demo.service.BusService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BusService busService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TicketRepository ticketRepository;
	
	@ModelAttribute
	public void commonUser(Principal p,Model m)
	{
		if(p!=null){
			String email = p.getName();
			User user = userRepo.findByEmail(email);
			m.addAttribute("user",user);
		}
	}
	
	@GetMapping("/profile")
	public String profile()
	{
		return "user_home";
	}
	
	@GetMapping("/search")
	public String search()
	{
		return "find_bus";
	}
	
	@PostMapping("/search")
    public String handleBusSearch(@RequestParam("startPlace") String startPlace,
                                  @RequestParam("destination") String destination,
                                  @RequestParam("date") String date,
                                  Model model,HttpSession session) {
        
		// Parse the selected date and extract the day of the week
	    LocalDate selectedDate = LocalDate.parse(date);
	    String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

	    // Find buses available every day or on the specific day of the week
	    List<Bus> searchResults = busService.findBusByDate(startPlace, destination, dayOfWeek);
        //System.out.println(searchResults);
        if (searchResults == null || searchResults.isEmpty()) {
            session.setAttribute("msg", "No Bus Available");
            return "find_bus";
        }
        
        // Add search results to the model to be displayed in the view
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("selectedDate",date);

        return "find_bus"; 
    }
	
	@GetMapping("/tickets")
    public String showTickets(Model model, Principal principal) {
        if (principal == null) {
            // Handle unauthenticated user
            return "redirect:/login";
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        List<Ticket> tickets = ticketRepository.findByUser(user);

        model.addAttribute("tickets", tickets);

        return "user_tickets";
    }
	

	@GetMapping("/changePassword")
	public String showChangePasswordForm() {
	    return "change_password_user";
	}

	@PostMapping("/changePassword")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 HttpSession session) {

        if (principal == null) {
            session.setAttribute("error", "User not authenticated");
            return "redirect:/login";  // Redirect to login page or handle as per your application flow
        }

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        // Validate old password
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            session.setAttribute("error", "Incorrect old password");
            return "redirect:/user/changePassword";
        }

        // Update password
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepository.save(user);

        session.setAttribute("msg", "Password updated successfully");
        return "redirect:/user/user_home";  
    }
	
	@GetMapping("/viewTicket/{ticketId}")
    public String viewTicket(@PathVariable("ticketId") int ticketId, Model model) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) {
            return "error/404"; 
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("title", "Ticket Details");
        return "ticket_details"; 
    }
	

}
