package com.example.cinema.api.application.mapper;

import com.example.cinema.api.domain.purchase.Purchase;import com.example.cinema.api.application.dto.purchase.PurchaseResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    PurchaseResponseDTO toResponseDTO(Purchase purchase);
}
