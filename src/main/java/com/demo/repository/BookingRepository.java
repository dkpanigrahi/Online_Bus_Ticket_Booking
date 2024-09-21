package com.demo.repository;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.entity.Booking;


public interface BookingRepository extends JpaRepository<Booking,Integer> {

	 Booking findBySeatNoAndBookingDate(int seatNo, LocalDate bookingDate);
}
