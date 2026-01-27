package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.entities.Purchase;import com.example.cinema.api.shared.dtos.purchase.PurchaseResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    PurchaseResponseDTO toResponseDTO(Purchase purchase);
}
