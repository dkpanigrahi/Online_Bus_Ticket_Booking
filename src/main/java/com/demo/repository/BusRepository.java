package com.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.demo.entity.Bus;

public interface BusRepository extends JpaRepository<Bus, Integer>  {

	List<Bus> findByStartPlaceAndDestination(String sp,String dest);
	
	
	@Query("SELECT b FROM Bus b WHERE b.startPlace = ?1 AND b.destination = ?2 AND (b.availableEveryDay = true OR ?3 MEMBER OF b.specificDays)")
    List<Bus> findBusByDate(String startPlace, String destination, String dayOfWeek);
	
	
}
