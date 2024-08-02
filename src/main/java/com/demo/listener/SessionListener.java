package com.demo.listener;

import com.demo.entity.Seat;
import com.demo.entity.Ticket;
import com.demo.repository.SeatRepository;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Retrieve the ticket from the session
        Ticket ticket = (Ticket) se.getSession().getAttribute("ticket");
        if (ticket != null) {
            // Release the seat lock if it is still in process
            Seat seat = seatRepository.findByBusAndSeatNo(ticket.getBus(), ticket.getSeatno());
            if (seat != null && seat.isInProcess() && seat.getExpirationTime().isAfter(LocalDateTime.now())) {
                seat.setInProcess(false);
                seat.setExpirationTime(null);
                seat.setBookingDate(null);								// Clear expiration time
                seatRepository.save(seat);
            }
        }
    }
}
