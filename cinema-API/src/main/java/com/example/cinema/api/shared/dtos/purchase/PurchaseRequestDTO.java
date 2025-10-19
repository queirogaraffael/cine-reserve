package com.example.cinema.api.shared.dtos.purchase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestDTO {

    private Set<Long> ticketIds;

}
