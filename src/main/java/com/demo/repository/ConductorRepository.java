package com.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.Conductor;

public interface ConductorRepository extends JpaRepository<Conductor, Integer> {

}
