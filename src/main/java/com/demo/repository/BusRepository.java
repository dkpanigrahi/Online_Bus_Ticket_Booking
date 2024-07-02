package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.Bus;

public interface BusRepository extends JpaRepository<Bus, Integer>  {

	List<Bus> findByStartPlaceAndDestination(String sp,String dest);
	
	
}
