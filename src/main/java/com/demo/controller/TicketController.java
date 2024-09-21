package com.demo.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.demo.dto.PaymentDetails;
import com.demo.entity.Booking;
import com.demo.entity.Bus;

import com.demo.entity.Ticket;
import com.demo.entity.User;
import com.demo.repository.BookingRepository;
import com.demo.repository.BusRepository;

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
    private TicketService ticketService;
    @Autowired
    private BookingRepository bookingRepository;

    @ModelAttribute
    public void commonUser(Principal p, Model m) {
        if (p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/bookTicket/{busId}")
    public String bookTicketForm(@PathVariable("busId") int busId, HttpSession session, Model model) {
        Bus bus = busRepository.findById(busId).orElse(null);
        if (bus == null) {
            session.setAttribute("error", "Bus not found!!!!");
            return "error_page";
        }
        
        
        List<String> seatOptions = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            seatOptions.add("Seat" + i); 
        }
        model.addAttribute("seatOptions", seatOptions); 
        model.addAttribute("bus", bus);
        model.addAttribute("ticketPrice", bus.getTicketPrice());

        return "book_ticket";
    }


    @Transactional
    @PostMapping("/bookTicket")
    public String bookTicket(@RequestParam("passengerName") String passengerName,
                             @RequestParam("seatno") int seatNo,
                             @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             @RequestParam("busId") int busId,
                             Principal principal,
                             HttpSession session,Model model) {
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

        // Check if the seat is already booked or in process for the selected date
        Booking existingBooking = bookingRepository.findBySeatNoAndBookingDate(seatNo, date);
        if (existingBooking != null && (existingBooking.isBooked() || existingBooking.isInProcess())) {
            session.setAttribute("error", "Seat " + seatNo + " is already booked or in process for this date");
            return "redirect:/user/bookTicket/" + busId;
        }

        // Create a new booking (temporarily lock the seat)
        Booking booking = new Booking();
        booking.setSeatNo(seatNo);
        booking.setPassengerName(passengerName);
        booking.setBookingDate(date);
        booking.setInProcess(true);  // Mark as "in process" while payment is being handled
        booking.setExpirationTime(LocalDateTime.now().plusMinutes(10)); // Temporary hold for 10 minutes
        booking.setUser(user);
        booking.setBus(bus);

        bookingRepository.save(booking);  // Save the booking

        // Store the booking in session for later use in payment
        session.setAttribute("ticket", booking);

        return "redirect:/user/paymentPage";
    }


    @GetMapping("/paymentPage")
    public String showPaymentPage(HttpSession session, Model model) {
        Booking booking = (Booking) session.getAttribute("ticket");
        if (booking == null) {
            session.setAttribute("error", "Ticket information missing");
            return "redirect:/user/bookTicket";
        }

        model.addAttribute("ticket", booking);
        model.addAttribute("paymentDetails", new PaymentDetails());
        model.addAttribute("ticketPrice", booking.getBus().getTicketPrice());

        return "payment_page";
    }

    @PostMapping("/dummy/payment/confirm")
    @Transactional
    public String confirmPayment(@ModelAttribute PaymentDetails paymentDetails, HttpSession session, Model model) {
        boolean paymentSuccess = processDummyPayment(paymentDetails);

        Booking booking = (Booking) session.getAttribute("ticket");
        if (booking == null) {
            session.setAttribute("error", "Ticket information missing");
            return "redirect:/user/bookTicket";
        }

        if (paymentSuccess) {
            session.setAttribute("paymentStatus", "Payment successful");

            try {
                // Mark the seat as booked
                booking.setBooked(true);
                booking.setInProcess(false);
                booking.setExpirationTime(null);
                bookingRepository.save(booking);

                // Create and save a new Ticket entity
                Ticket ticket = new Ticket();
                ticket.setPassengerName(booking.getPassengerName());
                ticket.setSeatno(booking.getSeatNo());
                ticket.setDate(booking.getBookingDate());
                ticket.setUser(booking.getUser());
                ticket.setBus(booking.getBus());

                ticketRepository.save(ticket); // Save the ticket to the database

                session.removeAttribute("ticket");
                return "redirect:/user/confirm_ticket?ticketId=" + ticket.getId() + "&bookingId=" + booking.getId();

            } catch (Exception e) {
                // Delete the booking if any error occurs while saving the ticket
                bookingRepository.delete(booking); // Remove booking from the database
                session.setAttribute("error", "An error occurred while saving ticket: " + e.getMessage());
                return "redirect:/user/paymentPage";
            }
        } else {
            // If payment fails, delete the booking
            bookingRepository.delete(booking); // Remove booking from the database
            session.setAttribute("paymentStatus", "Payment failed");
            return "redirect:/user/paymentPage";
        }
    }




    @GetMapping("/confirm_ticket")
    public String showConfirmationPage(@RequestParam("ticketId") int ticketId,
    								   @RequestParam("bookingId") int bookingId, Model model) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (booking == null) {
            return "error/404";
        }

        model.addAttribute("ticket", booking);
        model.addAttribute("ticketPrice", booking.getBus().getTicketPrice());
        model.addAttribute("paymentStatus", "Payment successful");
        return "confirm_ticket";
    }

    private boolean processDummyPayment(PaymentDetails paymentDetails) {
        // Mocking payment processing logic
        return true;
    }
}
