package com.example.cinema.api.shared.dtos.purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDTO {

    private Long ticketId;
    private Long movieSessionId;

}
