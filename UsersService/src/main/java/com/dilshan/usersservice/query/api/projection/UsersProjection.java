package com.dilshan.usersservice.query.api.projection;

import com.dilshan.core.model.PaymentDetails;
import com.dilshan.core.model.User;
import com.dilshan.core.queries.FetchUserPaymentDetailsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UsersProjection {

    @QueryHandler
    public User fetchUserPaymentQuery(FetchUserPaymentDetailsQuery query) {
        log.info("Fetch User Payment Details...");
        var paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("DILSHAN WIJETUNGA")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        var userPaymentDetails = User.builder()
                .firstName("Dilshan")
                .lastName("Wijetunga")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return userPaymentDetails;
    }
}
