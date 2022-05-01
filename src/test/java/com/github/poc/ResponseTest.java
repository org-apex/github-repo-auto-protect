package com.github.poc;

import com.github.poc.Response;
import com.microsoft.azure.functions.ExecutionContext;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseTest {

    @Test
    public void test() {
        Mono<String> result = new Response().apply(Mono.just(new String("foo")));
        assertThat(result).isEqualTo("SUCCESS!\n");
    }

    @Test
    public void start() {
        FunctionInvoker<String, String> handler = new FunctionInvoker<>(
        		Response.class);
        String result = handler.handleRequest(new String("foo"), new ExecutionContext() {
            @Override
            public Logger getLogger() {
                return Logger.getLogger(ResponseTest.class.getName());
            }

            @Override
            public String getInvocationId() {
                return "id1";
            }

            @Override
            public String getFunctionName() {
                return "hello";
            }
        });
        handler.close();
        assertThat(result).isEqualTo("Hello, foo!\n");
    }
}
