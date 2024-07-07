package com.dilshan.orderservice.command.api.controller;

import com.dilshan.orderservice.command.api.commands.CreateOrderCommand;
import com.dilshan.orderservice.command.api.rest.CreateOrderRestModel;
import com.dilshan.orderservice.core.data.model.OrderStatus;
import com.dilshan.orderservice.core.model.OrderSummary;
import com.dilshan.orderservice.query.api.queries.FindOrderQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private static final String USER_ID = "27b95829-4f3f-4ddf-8983-151ba010e35b";

    @PostMapping
    public OrderSummary createOrder(@Valid @RequestBody CreateOrderRestModel createOrderRestModel) {
        log.info("Create Order: {}", createOrderRestModel.toString());
        var orderId = UUID.randomUUID().toString();
        var orderCommand = CreateOrderCommand.builder()
                .addressId(createOrderRestModel.getAddressId())
                .orderId(orderId)
                .orderStatus(OrderStatus.CREATED)
                .productId(createOrderRestModel.getProductId())
                .quantity(createOrderRestModel.getQuantity())
                //TODO: Implement the userId
                .userId(USER_ID)
                .build();

        /*var orderIdRtnValue = this.commandGateway.sendAndWait(orderCommand);
        return "Create Order ID: %s".formatted(orderIdRtnValue);*/

        SubscriptionQueryResult<OrderSummary, OrderSummary> queryResult = this.queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                ResponseTypes.instanceOf(OrderSummary.class), ResponseTypes.instanceOf(OrderSummary.class));
        try {
            this.commandGateway.sendAndWait(orderCommand);
            return queryResult.updates().blockFirst();
        } finally {
            queryResult.close();
        }

    }

}
