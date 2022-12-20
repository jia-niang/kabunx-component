package com.kabunx.component.security.util;

import com.kabunx.component.common.dto.RestResponse;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ResponseUtils {

    public static <T> Mono<Void> defer(ServerWebExchange exchange, RestResponse<T> restResponse) {
        return Mono.defer(() -> Mono.just(exchange.getResponse())
                .flatMap(response -> {
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    DataBufferFactory dataBufferFactory = response.bufferFactory();
                    DataBuffer buffer = dataBufferFactory.wrap(restResponse.toJsonBytes());
                    return response.writeWith(Mono.just(buffer)).doOnError(error -> DataBufferUtils.release(buffer));
                }));
    }
}
