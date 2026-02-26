package com.example.cinema.api.application.mapper;

import com.example.cinema.api.application.dto.purchase.PurchaseIdempotencyResponseDTO;
import com.example.cinema.api.domain.purchase.Purchase;import com.example.cinema.api.application.dto.purchase.PurchaseResponseDTO;
import com.example.cinema.api.domain.ticket.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseMapper {

    @Mapping(target = "ticketIds", source = "tickets")
    PurchaseResponseDTO toResponseDTO(Purchase purchase);

    PurchaseIdempotencyResponseDTO toPurchaseIdempotencyResponseDTO(Purchase purchase);


    default List<Long> mapTickets(List<Ticket> tickets) {

        if (tickets == null) {
            return Collections.emptyList();
        }

        return tickets.stream()
                .map(Ticket::getId)
                .toList();
    }

}