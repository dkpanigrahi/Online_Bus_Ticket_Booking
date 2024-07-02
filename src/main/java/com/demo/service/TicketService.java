package com.demo.service;

import java.util.List;

import com.demo.entity.Bus;
import com.demo.entity.Ticket;
import com.demo.entity.User;

public interface TicketService {

	public Ticket saveTickets(Ticket ticket);
	
	public List<Ticket> getAllTicket();
	
	public Ticket getTicketById(int id);
	
	public List<Ticket> getTicketsByUser(User user);
	
	public List<Ticket> getTicketByBus(Bus bus);
	
	public boolean deleteTicket(int id);
}
