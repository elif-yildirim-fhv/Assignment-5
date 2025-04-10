package sem4.ea.ss25.battleship.assignment2.board_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.BoardMessage;
import sem4.ea.ss25.battleship.assignment2.board_service.service.BoardMessageSender;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	@Autowired
	private BoardMessageSender boardMessageSender;

	@PostMapping("/create")
	public ResponseEntity<String> createBoard() {
		BoardMessage message = new BoardMessage();
		boardMessageSender.sendCommand("CREATE_BOARD", message);
		return ResponseEntity.accepted().body("Board creation request sent");
	}

	@PostMapping("/placeShip")
	public ResponseEntity<String> placeShip(
			@RequestParam Long boardId,
			@RequestParam Long playerId,
			@RequestParam int length,
			@RequestParam int x,
			@RequestParam int y,
			@RequestParam boolean isHorizontal) {

		BoardMessage message = new BoardMessage();
		message.setBoardId(boardId);
		message.setPlayerId(playerId);
		message.setLength(length);
		message.setX(x);
		message.setY(y);
		message.setHorizontal(isHorizontal);

		boardMessageSender.sendCommand("PLACE_SHIP", message);
		return ResponseEntity.accepted().body("Ship placement request sent");
	}

	@PostMapping("/guess")
	public ResponseEntity<String> makeGuess(
			@RequestParam Long gameId,
			@RequestParam Long boardId,
			@RequestParam Long playerId,
			@RequestParam Long opponentId,
			@RequestParam int x,
			@RequestParam int y) {

		BoardMessage message = new BoardMessage();
		message.setGameId(gameId);
		message.setBoardId(boardId);
		message.setPlayerId(playerId);
		message.setOpponentId(opponentId);
		message.setX(x);
		message.setY(y);

		boardMessageSender.sendCommand("MAKE_GUESS", message);
		return ResponseEntity.accepted().body("Guess request sent");
	}

	@PostMapping("/endGame")
	public ResponseEntity<String> endGame(@RequestParam Long boardId) {
		BoardMessage message = new BoardMessage();
		message.setBoardId(boardId);
		boardMessageSender.sendCommand("END_GAME", message);
		return ResponseEntity.accepted().body("Game end request sent");
	}
}