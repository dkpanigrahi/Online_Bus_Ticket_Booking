package com.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.entity.Bus;
import com.demo.repository.BusRepository;

import jakarta.transaction.Transactional;

@Service
public class BusServiceImpl implements BusService {

	@Autowired
	private BusRepository busRepository;
	
	@Override
	public Bus savebus(Bus bus) {
		
		return busRepository.save(bus);
	}

	@Override
    public Bus getBusById(int busId) {
        return busRepository.findById(busId).orElse(null);
    }

	@Override
	public List<Bus> findBus(String startp, String destp) {
		List<Bus> buslist = busRepository.findByStartPlaceAndDestination(startp, destp);
		return buslist;
	}

	@Override
	public Bus updatebus(Bus bus) {
		
		return busRepository.save(bus);
	}

	@Override
    @Transactional
    public void deleteBusById(int id) {
        busRepository.deleteById(id);
    }

	@Override
	public List<Bus> getAllBus() {
		
		List<Bus> buslist = busRepository.findAll();
		return buslist;
	}


}
