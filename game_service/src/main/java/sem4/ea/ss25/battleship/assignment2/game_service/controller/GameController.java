package sem4.ea.ss25.battleship.assignment2.game_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem4.ea.ss25.battleship.assignment2.game_service.domain.GameMessage;
import sem4.ea.ss25.battleship.assignment2.game_service.service.GameMessageSender;

@RestController
@RequestMapping("/api/game")
public class GameController {

	@Autowired
	private GameMessageSender gameMessageSender;

	@PostMapping("/create")
	public ResponseEntity<String> createGame() {
		GameMessage message = new GameMessage();
		gameMessageSender.sendCommand("CREATE_GAME", message);
		return ResponseEntity.accepted().body("Game creation request sent");
	}

	@PostMapping("/{gameId}/addPlayer")
	public ResponseEntity<String> addPlayerToGame(
			@PathVariable Long gameId,
			@RequestParam Long playerId) {

		GameMessage message = new GameMessage();
		message.setGameId(gameId);
		message.setPlayerId(playerId);

		gameMessageSender.sendCommand("ADD_PLAYER", message);
		return ResponseEntity.accepted().body("Player added to game request sent");
	}

	@PostMapping("/{gameId}/end")
	public ResponseEntity<String> endGame(@PathVariable Long gameId) {
		GameMessage message = new GameMessage();
		message.setGameId(gameId);
		gameMessageSender.sendCommand("END_GAME", message);
		return ResponseEntity.accepted().body("Game end request sent");
	}

	@GetMapping("/{gameId}")
	public ResponseEntity<String> getGameStatus(@PathVariable Long gameId) {
		GameMessage message = new GameMessage();
		message.setGameId(gameId);
		gameMessageSender.sendCommand("GET_STATUS", message);
		return ResponseEntity.accepted().body("Game status request sent");
	}
}