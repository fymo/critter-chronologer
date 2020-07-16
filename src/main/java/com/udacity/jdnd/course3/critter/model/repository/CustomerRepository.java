package com.udacity.jdnd.course3.critter.model.repository;

import com.udacity.jdnd.course3.critter.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
