package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.Bus;
import com.demo.entity.Ticket;
import com.demo.entity.User;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

	public List<Ticket> findByUser(User user);
	
	public List<Ticket> findByBus(Bus bus);
}
