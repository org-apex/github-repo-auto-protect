/**
 * MIT License
 */

package com.github.poc;


import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.function.Function;

@Component
public class Response implements Function<Mono<String>, Mono<String>> {


	@Override
	public Mono<String> apply(Mono<String> t) {
		// TODO Auto-generated method stub
        return t.map(user -> new String("SUCCESS"));
	}
}
