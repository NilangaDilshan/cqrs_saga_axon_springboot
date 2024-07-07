package com.dilshan.usersservice.query.api.controller;

import com.dilshan.core.model.User;
import com.dilshan.core.queries.FetchUserPaymentDetailsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UsersQueryController {

    private final QueryGateway queryGateway;

    @GetMapping("/{userId}/payment-details")
    public User getUserPaymentDetails(@PathVariable String userId) {
        log.info("Received request to get user payment details for user id: {}", userId);
        var query = new FetchUserPaymentDetailsQuery(userId);
        return this.queryGateway.query(query, User.class).join();
    }

}
