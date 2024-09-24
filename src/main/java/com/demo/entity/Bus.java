package com.demo.entity;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String busNo;
    
    private String startPlace;
    
    private String destination;
    
    private LocalTime departureTime;
    
    private boolean availableEveryDay;

    @ElementCollection
    private List<String> specificDays;
    
    private int totalSeats;
    
    private int ticketPrice;

    @OneToOne
    private Driver driver;

    @OneToOne
    private Conductor conductor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBusNo() {
        return busNo;
    }

    public void setBusNo(String busNo) {
        this.busNo = busNo;
    }

    public String getStartPlace() {
        return startPlace;
    }

    public void setStartPlace(String startPlace) {
        this.startPlace = startPlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

   
    public String getDepartureTime() {
        if (departureTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            return departureTime.format(formatter);
        }
        return null;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public boolean isAvailableEveryDay() {
        return availableEveryDay;
    }

    public void setAvailableEveryDay(boolean availableEveryDay) {
        this.availableEveryDay = availableEveryDay;
    }

    public List<String> getSpecificDays() {
        return specificDays;
    }

    public void setSpecificDays(List<String> specificDays) {
        this.specificDays = specificDays;
    }
}
