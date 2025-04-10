package sem4.ea.ss25.battleship.assignment2.api_gateway.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sem4.ea.ss25.battleship.assignment2.api_gateway.dto.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game-details")
public class GameDetailsController {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public GameDetailsController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{gameId}")
    @CircuitBreaker(name = "gameDetailsCircuitBreaker", fallbackMethod = "getGameDetailsFallback")
    public Mono<ResponseEntity<GameDetailsDTO>> getGameDetails(@PathVariable Long gameId) {
        // Step 1: Get game information
        return webClientBuilder.build()
                .get()
                .uri("lb://game-service/api/game/{gameId}", gameId)
                .retrieve()
                .bodyToMono(GameDTO.class)
                .flatMap(game -> {
                    // Step 2: Get board information
                    Mono<BoardDTO> boardMono = webClientBuilder.build()
                            .get()
                            .uri("lb://board-service/api/board/{boardId}", game.boardId())
                            .retrieve()
                            .bodyToMono(BoardDTO.class);

                    // Step 3: Get player information for each player
                    List<Mono<PlayerDTO>> playerMonos = game.playerIds().stream()
                            .map(playerId -> webClientBuilder.build()
                                    .get()
                                    .uri("lb://player-service/api/player/{playerId}", playerId)
                                    .retrieve()
                                    .bodyToMono(PlayerDTO.class))
                            .toList();

                    // Combine all player monos into a single list
                    Mono<List<PlayerDTO>> playersMono = Mono.zip(
                            playerMonos,
                            objects -> {
                                List<PlayerDTO> players = new ArrayList<>();
                                for (Object obj : objects) {
                                    players.add((PlayerDTO) obj);
                                }
                                return players;
                            });

                    // Combine board and players information
                    return Mono.zip(boardMono, playersMono, (board, players) ->
                            new GameDetailsDTO(game.id(), game.boardId(), players, board));
                })
                .map(ResponseEntity::ok);
    }

    public Mono<ResponseEntity<GameDetailsDTO>> getGameDetailsFallback(Long gameId, Exception ex) {
        // Return a fallback response when services are unavailable
        return Mono.just(ResponseEntity.ok(
                new GameDetailsDTO(
                        gameId,
                        -1L,
                        List.of(new PlayerDTO(-1L, "Service Unavailable", 0)),
                        new BoardDTO(-1L, List.of())
                )
        ));
    }
}
