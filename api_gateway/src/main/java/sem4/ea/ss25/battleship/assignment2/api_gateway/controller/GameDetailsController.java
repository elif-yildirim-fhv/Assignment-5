package sem4.ea.ss25.battleship.assignment2.api_gateway.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sem4.ea.ss25.battleship.assignment2.api_gateway.dto.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/game-details")
public class GameDetailsController {
	private final WebClient.Builder webClientBuilder;
	private final ReactiveCircuitBreakerFactory circuitBreakerFactory;

	@Autowired
	public GameDetailsController(WebClient.Builder webClientBuilder,
							   ReactiveCircuitBreakerFactory circuitBreakerFactory) {
		this.webClientBuilder = webClientBuilder;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	@GetMapping("/{gameId}")
	public Mono<ResponseEntity<GameDetailsDTO>> getGameDetails(@PathVariable Long gameId) {
		ReactiveCircuitBreaker gameCircuitBreaker = 
			circuitBreakerFactory.create("gameServiceCircuitBreaker");
		ReactiveCircuitBreaker boardCircuitBreaker = 
			circuitBreakerFactory.create("boardServiceCircuitBreaker");
		ReactiveCircuitBreaker playerCircuitBreaker = 
			circuitBreakerFactory.create("playerServiceCircuitBreaker");


		return gameCircuitBreaker.run(
			webClientBuilder.build()
				.get()
				.uri("lb://game-service/api/game/{gameId}", gameId)
				.retrieve()
				.bodyToMono(GameDTO.class)
				.timeout(Duration.ofSeconds(10)),
			throwable -> {
				return Mono.just(new GameDTO(gameId, -1L, List.of()));
			}
		).flatMap(game -> {
			Mono<BoardDTO> boardMono;
			if (game.boardId() != null && game.boardId() != -1L) {
				boardMono = boardCircuitBreaker.run(
					webClientBuilder.build()
						.get()
						.uri("lb://board-service/api/board/{boardId}", game.boardId())
						.retrieve()
						.bodyToMono(BoardDTO.class)
						.timeout(Duration.ofSeconds(10)),
					throwable -> {
						return Mono.just(new BoardDTO(-1L, List.of(), List.of()));
					}
				);
			} else {
				boardMono = Mono.just(new BoardDTO(-1L, List.of(), List.of()));
			}

			List<Mono<PlayerDTO>> playerMonos = game.playerIds().stream()
				.map(playerId -> {
					return playerCircuitBreaker.run(
						webClientBuilder.build()
							.get()
							.uri("lb://player-service/api/player/{playerId}", playerId)
							.retrieve()
							.bodyToMono(PlayerDTO.class)
							.timeout(Duration.ofSeconds(10)),
						throwable -> {
							return Mono.just(new PlayerDTO(-1L, "Service Unavailable", 0));
						}
					);
				})
				.toList();

			Mono<List<PlayerDTO>> playersMono;
			if (playerMonos.isEmpty()) {
				playersMono = Mono.just(List.of());
			} else {
				playersMono = Mono.zip(
					playerMonos,
					objects -> {
						List<PlayerDTO> players = new ArrayList<>();
						for (Object obj : objects) {
							players.add((PlayerDTO) obj);
						}
						return players;
					}
				);
			}

			return Mono.zip(boardMono, playersMono, (board, players) -> {
				return ResponseEntity.ok(new GameDetailsDTO(game.id(), game.boardId(), players, board));
			});
		});
	}
}
