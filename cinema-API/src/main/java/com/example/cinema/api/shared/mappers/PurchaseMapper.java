package com.example.cinema.api.shared.mappers;

import com.example.cinema.api.domain.entities.Purchase;
import com.example.cinema.api.shared.dtos.purchase.PurchaseRequestDTO;
import com.example.cinema.api.shared.dtos.purchase.PurchaseResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    Purchase toEntity(PurchaseRequestDTO purchaseRequestDTO);

    PurchaseResponseDTO toResponseDTO(Purchase purchase);
}
