package com.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.dto.PaymentDetails;
import com.demo.entity.Bus;
import com.demo.entity.Seat;
import com.demo.entity.Ticket;
import com.demo.entity.User;
import com.demo.repository.BusRepository;
import com.demo.repository.SeatRepository;
import com.demo.repository.TicketRepository;
import com.demo.repository.UserRepository;
import com.demo.service.TicketService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class TicketController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private TicketService ticketService;

    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/bookTicket/{busId}")
    public String bookTicketForm(@PathVariable("busId") int busId,
                                 @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                 HttpSession session, Model model) {
        Bus bus = busRepository.findById(busId).orElse(null);
        if (bus == null) {
            session.setAttribute("error", "Bus not found!!!!");
            return "error_page";
        }

        List<Seat> seats = seatRepository.findByBusAndBookingDate(bus, date);
        List<Seat> bookedSeats = seats.stream()
                                      .filter(Seat::isBooked)
                                      .collect(Collectors.toList());

        model.addAttribute("bus", bus);
        model.addAttribute("date", date);
        model.addAttribute("bookedSeats", bookedSeats);
        model.addAttribute("ticketPrice", bus.getTicketPrice()); // Add ticket price to the model

        return "book_ticket";
    }

    @PostMapping("/bookTicket")
    public String bookTicket(@RequestParam("passengerName") String passengerName,
                             @RequestParam("seatno") String seatno,
                             @RequestParam("date") LocalDate date,
                             @RequestParam("busId") int busId,
                             Principal principal,
                             Model model, HttpSession session) {
        if (principal == null) {
            session.setAttribute("error", "User not authenticated");
            return "redirect:/user/bookTicket/" + busId;
        }

        String email = principal.getName();
        User user = userRepo.findByEmail(email);
        Bus bus = busRepository.findById(busId).orElse(null);
        if (bus == null) {
            session.setAttribute("error", "Bus not found");
            return "redirect:/user/bookTicket/" + busId;
        }

        Seat seat = seatRepository.findByBusAndSeatNo(bus, seatno);
        if (seat == null || seat.isBooked()) {
            session.setAttribute("error", "Seat is already booked or does not exist");
            return "redirect:/user/bookTicket/" + busId;
        }

        try {
            seat.setBooked(true);
            seatRepository.save(seat);

            Ticket ticket = new Ticket();
            ticket.setPassengerName(passengerName);
            ticket.setSeatno(seatno);
            ticket.setDate(date);
            ticket.setUser(user);
            ticket.setBus(bus);

            ticketRepository.save(ticket);
            session.setAttribute("ticket", ticket); // Store ticket in session

            return "redirect:/user/paymentPage"; // Redirect to payment page after booking
        } catch (Exception e) {
            session.setAttribute("error", "An error occurred while booking the ticket: " + e.getMessage());
            return "redirect:/user/bookTicket/" + busId;
        }
    }

    @GetMapping("/paymentPage")
    public String showPaymentPage(HttpSession session, Model model) {
        Ticket ticket = (Ticket) session.getAttribute("ticket");
        if (ticket == null) {
            session.setAttribute("error", "Ticket information missing");
            return "redirect:/user/bookTicket/" + ticket.getBus().getId();
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("paymentDetails", new PaymentDetails()); // To bind payment form
        model.addAttribute("ticketPrice", ticket.getBus().getTicketPrice()); // Add ticket price to the model

        return "payment_page";
    }

	/*
	 * @PostMapping("/dummy/payment/confirm") public String
	 * confirmPayment(@ModelAttribute PaymentDetails paymentDetails, HttpSession
	 * session, Model model) { // Simulate payment confirmation boolean
	 * paymentSuccess = processDummyPayment(paymentDetails);
	 * 
	 * if (paymentSuccess) { session.setAttribute("paymentStatus",
	 * "Payment successful");
	 * 
	 * // Retrieve ticket from session Ticket ticket = (Ticket)
	 * session.getAttribute("ticket"); if (ticket == null) {
	 * session.setAttribute("error", "Ticket information missing"); return
	 * "redirect:/user/bookTicket/" + paymentDetails.getBusId(); }
	 * 
	 * // Save ticket to database try { ticketRepository.save(ticket);
	 * session.removeAttribute("ticket"); // Clear ticket from session after payment
	 * 
	 * return "redirect:/user/confirmTicket"; // Redirect to confirmation page }
	 * catch (Exception e) { session.setAttribute("error",
	 * "An error occurred while saving ticket: " + e.getMessage()); return
	 * "redirect:/user/paymentPage"; } } else {
	 * session.setAttribute("paymentStatus", "Payment failed"); return
	 * "redirect:/user/paymentPage"; } }
	 */
    
    @PostMapping("/dummy/payment/confirm")
    public String confirmPayment(@ModelAttribute PaymentDetails paymentDetails, HttpSession session, Model model) {
        boolean paymentSuccess = processDummyPayment(paymentDetails);

        if (paymentSuccess) {
            session.setAttribute("paymentStatus", "Payment successful");
            
            Ticket ticket = (Ticket) session.getAttribute("ticket");
            if (ticket == null) {
                session.setAttribute("error", "Ticket information missing");
                return "redirect:/user/bookTicket/" + paymentDetails.getBusId();
            }

            try {
                ticketRepository.save(ticket);
                session.removeAttribute("ticket"); // Clear ticket from session after payment
                return "redirect:/user/confirm_ticket?ticketId=" + ticket.getId(); // Redirect to confirmation page with ticket ID
            } catch (Exception e) {
                session.setAttribute("error", "An error occurred while saving ticket: " + e.getMessage());
                return "redirect:/user/paymentPage";
            }
        } else {
            session.setAttribute("paymentStatus", "Payment failed");
            return "redirect:/user/paymentPage";
        }
    }

    @GetMapping("/confirm_ticket")
    public String showConfirmationPage(@RequestParam("ticketId") int ticketId, Model model) {
        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            return "error/404";
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("ticketPrice", ticket.getBus().getTicketPrice());
        model.addAttribute("paymentStatus", "Payment successful"); // Assuming payment was successful
        return "confirm_ticket";
    }

    private boolean processDummyPayment(PaymentDetails paymentDetails) {
        // Mocking payment processing logic
        // Simulate successful payment confirmation for demonstration
        // You can add your own logic here for payment integration
        return true;
    }


	/*
	 * @GetMapping("/confirm_ticket") public String
	 * showConfirmationPage(@RequestParam("ticketId") int ticketId, Model model) {
	 * Ticket ticket = ticketService.getTicketById(ticketId); // Assuming
	 * ticketService.findById(ticketId) returns a Ticket object if (ticket == null)
	 * { // Handle the case where the ticket is not found return "error/404"; }
	 * model.addAttribute("ticket", ticket); model.addAttribute("ticketPrice",
	 * calculateTicketPrice(ticket)); // Add the ticket price to the model return
	 * "confirm_ticket"; }
	 */

    private double calculateTicketPrice(Ticket ticket) {
        // Implement your ticket price calculation logic here
        return 0.0; // Replace with actual calculation
    }
}

