package com.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.Bus;
import com.demo.entity.Seat;

public interface SeatRepository extends JpaRepository<Seat, Integer> {

	Seat findByBusAndSeatNo(Bus bus, String seatNo);
	
	 List<Seat> findByBusAndBookingDate(Bus bus, LocalDate bookingDate);
}
