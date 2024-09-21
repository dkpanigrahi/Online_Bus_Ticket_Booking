package com.demo.listener;

import com.demo.entity.Booking;
import com.demo.entity.Ticket;
import com.demo.repository.BookingRepository;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private BookingRepository bookingRepository; // BookingRepository injected

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Retrieve the booking from the session
        Booking booking = (Booking) se.getSession().getAttribute("ticket");
        if (booking != null && booking.isInProcess()) {
            // Release the seat lock if the booking is still in process and not expired
            if (booking.getExpirationTime() != null && booking.getExpirationTime().isAfter(LocalDateTime.now())) {
                booking.setInProcess(false); 
                booking.setExpirationTime(null);
                bookingRepository.save(booking); 
            }
        }
    }
}
