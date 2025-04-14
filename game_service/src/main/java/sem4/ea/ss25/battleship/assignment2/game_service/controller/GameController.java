package sem4.ea.ss25.battleship.assignment2.game_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem4.ea.ss25.battleship.assignment2.game_service.domain.GameMessage;
import sem4.ea.ss25.battleship.assignment2.game_service.dto.GameDTO;
import sem4.ea.ss25.battleship.assignment2.game_service.service.GameMessageSender;
import sem4.ea.ss25.battleship.assignment2.game_service.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {

	@Autowired
	private GameMessageSender gameMessageSender;

	@Autowired
	private GameService gameService;

	@PostMapping("/create")
	public ResponseEntity<GameDTO> createGame() {
		GameDTO game = gameService.createGame();
		GameMessage message = new GameMessage();
		message.setGameId(game.id());
		message.setBoardId(game.boardId());
		gameMessageSender.sendCommand("CREATE_GAME", message);
		return ResponseEntity.ok(game);
	}

	@PostMapping("/{gameId}/addPlayer")
	public ResponseEntity<GameDTO> addPlayerToGame(
			@PathVariable Long gameId,
			@RequestParam Long playerId) {

		GameMessage message = new GameMessage();
		message.setGameId(gameId);
		message.setPlayerId(playerId);

		gameMessageSender.sendCommand("ADD_PLAYER", message);
		GameDTO updatedGame = gameService.addPlayerToGame(gameId, playerId);
		return ResponseEntity.ok(updatedGame);
	}

	@PostMapping("/{gameId}/end")
	public ResponseEntity<GameDTO> endGame(@PathVariable Long gameId) {
		GameMessage message = new GameMessage();
		message.setGameId(gameId);
		gameMessageSender.sendCommand("END_GAME", message);
		String result = gameService.endGame(gameId);
		GameDTO game = gameService.getGame(gameId);
		return ResponseEntity.ok(game);
	}

	@GetMapping("/{gameId}")
	public ResponseEntity<GameDTO> getGameStatus(@PathVariable Long gameId) {
		GameMessage message = new GameMessage();
		message.setGameId(gameId);
		gameMessageSender.sendCommand("GET_STATUS", message);
		GameDTO game = gameService.getGame(gameId);
		return ResponseEntity.ok(game);
	}
}