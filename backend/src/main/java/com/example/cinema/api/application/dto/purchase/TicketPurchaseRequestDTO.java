package com.example.cinema.api.application.dto.purchase;

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
