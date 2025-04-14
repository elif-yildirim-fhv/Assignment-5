package sem4.ea.ss25.battleship.assignment2.board_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.BoardMessage;
import sem4.ea.ss25.battleship.assignment2.board_service.service.BoardMessageSender;
import sem4.ea.ss25.battleship.assignment2.board_service.service.BoardService;
import sem4.ea.ss25.battleship.assignment2.board_service.dto.BoardDTO;
import sem4.ea.ss25.battleship.assignment2.board_service.dto.GuessResultDTO;

@RestController
@RequestMapping("/api/board")
public class BoardController {

	@Autowired
	private BoardMessageSender boardMessageSender;

	@Autowired
	private BoardService boardService;

	@GetMapping("/{boardId}")
	public ResponseEntity<BoardDTO> getBoard(@PathVariable Long boardId) {
		BoardDTO board = boardService.getBoard(boardId);
		return ResponseEntity.ok(board);
	}

	@PostMapping("/create")
	public ResponseEntity<Long> createBoard() {
		BoardDTO board = boardService.createBoard();
		BoardMessage message = new BoardMessage();
		message.setBoardId(board.id());
		boardMessageSender.sendCommand("CREATE_BOARD", message);
		return ResponseEntity.ok(board.id());
	}

	@PostMapping("/placeShip")
	public ResponseEntity<BoardDTO> placeShip(
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
		BoardDTO board = boardService.placeShip(boardId, playerId, length, x, y, isHorizontal);
		return ResponseEntity.ok(board);
	}

	@PostMapping("/guess")
	public ResponseEntity<GuessResultDTO> makeGuess(
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
		GuessResultDTO result = boardService.makeGuess(gameId, boardId, playerId, opponentId, x, y);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/endGame")
	public ResponseEntity<BoardDTO> endGame(@RequestParam Long boardId) {
		BoardMessage message = new BoardMessage();
		message.setBoardId(boardId);
		boardMessageSender.sendCommand("END_GAME", message);
		BoardDTO board = boardService.endGame(boardId);
		return ResponseEntity.ok(board);
	}
}