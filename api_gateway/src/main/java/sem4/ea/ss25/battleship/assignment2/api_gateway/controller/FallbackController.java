package sem4.ea.ss25.battleship.assignment2.api_gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/game")
    public Mono<ResponseEntity<Map<String, String>>> gameServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Game service is currently unavailable");
        return Mono.just(ResponseEntity.ok(response));
    }

    @GetMapping("/board")
    public Mono<ResponseEntity<Map<String, String>>> boardServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Board service is currently unavailable");
        return Mono.just(ResponseEntity.ok(response));
    }

    @GetMapping("/player")
    public Mono<ResponseEntity<Map<String, String>>> playerServiceFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Player service is currently unavailable");
        return Mono.just(ResponseEntity.ok(response));
    }
}
