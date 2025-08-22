package com.example.cinema.api.domain.repositories;


import com.example.cinema.api.domain.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

}
