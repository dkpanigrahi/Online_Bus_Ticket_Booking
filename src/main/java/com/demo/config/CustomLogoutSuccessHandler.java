package com.demo.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.demo.entity.User;
import com.demo.repository.BookingRepository;
import com.demo.repository.UserRepository;
import com.demo.service.BookingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	
	@Autowired
	private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getName() != null) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            // Find and delete bookings that are still "in process" for this user
            if (user != null) {
            	bookingService.deleteInProcessBookings(user);
            }
        }
        response.sendRedirect("/signin?logout");
    }

}
