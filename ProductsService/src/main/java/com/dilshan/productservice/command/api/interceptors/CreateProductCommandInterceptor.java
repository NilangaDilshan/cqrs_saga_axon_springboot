package com.dilshan.productservice.command.api.interceptors;

import com.dilshan.productservice.command.api.commands.CreateProductCommand;
import com.dilshan.productservice.core.data.repository.ProductLookupEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductLookupEntityRepository productLookupEntityRepository;

    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(@Nonnull List<? extends CommandMessage<?>> messages) {
        return (index, command) -> {
            log.info("Intercepted command: {}", command.getPayloadType());
            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();
                this.productLookupEntityRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle())
                        .ifPresent(productLookupEntity -> {
                            throw new IllegalStateException("Product with productId or title already exists ProductID: %s Title: %s"
                                    .formatted(productLookupEntity.getProductId(), productLookupEntity.getTitle()));
                        });
            }
            return command;
        };
    }
}
