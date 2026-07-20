package com.example.cinema.api.application.mapper;

import com.example.cinema.api.application.dto.order.OrderItemResponseDTO;
import com.example.cinema.api.application.dto.order.OrderResponseDTO;
import com.example.cinema.api.domain.order.Order;
import com.example.cinema.api.domain.order.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", expression = "java(order.getStatus().getValue())")
    @Mapping(target = "sessionId", source = "movieSession.id")
    @Mapping(target = "items", source = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);

    @Mapping(target = "ticketTypeId", source = "ticketType.id")
    @Mapping(target = "ticketTypeName", source = "ticketType.name")
    @Mapping(target = "category", expression = "java(item.getTicketType().getCategory().name())")
    OrderItemResponseDTO toItemResponseDTO(OrderItem item);
}
