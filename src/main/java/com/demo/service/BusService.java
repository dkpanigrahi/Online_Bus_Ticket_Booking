package com.demo.service;

import java.util.List;

import com.demo.entity.Bus;

public interface BusService {
	
	public Bus savebus(Bus bus);
	
	public List<Bus> getAllBus();
	
	public Bus getBusById(int id);
	
	public List<Bus> findBus(String from, String to);
	
	public Bus updatebus(Bus bus);
	
	public void deleteBusById(int id);
	
	public List<Bus> findBusByDate(String startPlace, String destination, String dayOfWeek);
	
	
	
}
