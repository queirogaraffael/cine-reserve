package com.example.cinema.api.shared.dtos.purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPurchaseRequestDTO {

    private List<TicketItemDTO> items;
}
