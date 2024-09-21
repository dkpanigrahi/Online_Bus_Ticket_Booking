package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.entity.User;
import com.demo.repository.BookingRepository;

import jakarta.transaction.Transactional;

@Service
public class BookingService {
	
	@Autowired
	private BookingRepository bookingRepository;

	@Transactional
    public void deleteInProcessBookings(User user) {
        bookingRepository.deleteByUserAndInProcess(user, true);
    }
}
