package sem4.ea.ss25.battleship.assignment2.api_gateway.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(GameDetailsController.class);
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
		log.info("Fetching game details for gameId: {}", gameId);

		ReactiveCircuitBreaker gameCircuitBreaker = 
			circuitBreakerFactory.create("gameServiceCircuitBreaker");
		ReactiveCircuitBreaker boardCircuitBreaker = 
			circuitBreakerFactory.create("boardServiceCircuitBreaker");
		ReactiveCircuitBreaker playerCircuitBreaker = 
			circuitBreakerFactory.create("playerServiceCircuitBreaker");

		// Step 1: Get game information
		return gameCircuitBreaker.run(
			webClientBuilder.build()
				.get()
				.uri("lb://game-service/api/game/{gameId}", gameId)
				.retrieve()
				.bodyToMono(GameDTO.class),
			throwable -> {
				log.error("Game service failed", throwable);
				return Mono.just(new GameDTO(gameId, -1L, List.of()));
			}
		).flatMap(game -> {
			log.info("Retrieved game: {}", game);

			// Step 2: Get board information
			Mono<BoardDTO> boardMono;
			if (game.boardId() == null || game.boardId() == -1L) {
				log.warn("Game has invalid boardId: {}, skipping board service call", game.boardId());
				boardMono = Mono.just(new BoardDTO(-1L, List.of(), List.of()));
			} else {
				boardMono = boardCircuitBreaker.run(
					webClientBuilder.build()
						.get()
						.uri("lb://board-service/api/board/{boardId}", game.boardId())
						.retrieve()
						.bodyToMono(BoardDTO.class),
					throwable -> {
						log.error("Board service failed for boardId: {}", game.boardId(), throwable);
						return Mono.just(new BoardDTO(-1L, List.of(), List.of()));
					}
				);
			}
			boardMono = boardMono.doOnNext(board -> log.info("Retrieved board: {}", board));

			// Step 3: Get player information for each player
			List<Mono<PlayerDTO>> playerMonos = game.playerIds().stream()
				.map(playerId -> {
					log.info("Fetching player info for playerId: {}", playerId);
					return playerCircuitBreaker.run(
						webClientBuilder.build()
							.get()
							.uri("lb://player-service/api/player/{playerId}", playerId)
							.retrieve()
							.bodyToMono(PlayerDTO.class),
						throwable -> {
							log.error("Player service failed for playerId: {}", playerId, throwable);
							return Mono.just(new PlayerDTO(-1L, "Service Unavailable", 0));
						}
					).doOnNext(player -> log.info("Retrieved player: {}", player));
				})
				.toList();

			// Combine all player monos into a single list
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
			playersMono = playersMono.doOnNext(players -> log.info("Combined players: {}", players));

			// Combine board and players information
			return Mono.zip(boardMono, playersMono, (board, players) -> {
				log.info("Creating GameDetailsDTO with board: {} and players: {}", board, players);
				return ResponseEntity.ok(new GameDetailsDTO(game.id(), game.boardId(), players, board));
			});
		});
	}
}
