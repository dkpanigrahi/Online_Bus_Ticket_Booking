package com.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.entity.Seat;
import com.demo.repository.SeatRepository;

@Service
public class SeatServiceImpl implements SeatService {

	
	@Autowired
	private SeatRepository seatRepository;

	@Override
	public void saveAll(List<Seat> seats) {
		// TODO Auto-generated method stub
		seatRepository.saveAll(seats);
		
	}

}
