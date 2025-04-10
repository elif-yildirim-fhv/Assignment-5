package sem4.ea.ss25.battleship.assignment2.board_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.Board;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.BoardMessage;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.Cell;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.Ship;
import sem4.ea.ss25.battleship.assignment2.board_service.dto.BoardDTO;
import sem4.ea.ss25.battleship.assignment2.board_service.dto.CellDTO;
import sem4.ea.ss25.battleship.assignment2.board_service.dto.ShipDTO;
import sem4.ea.ss25.battleship.assignment2.board_service.repository.BoardRepository;
import sem4.ea.ss25.battleship.assignment2.board_service.repository.ShipRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private ShipRepository shipRepository;

	@Autowired
	private BoardMessageSender boardMessageSender;

	public BoardDTO createBoard() {
		Board board = new Board();
		board.initializeBoard(10);
		board = boardRepository.save(board);

		return convertToDTO(board);
	}

	public ShipDTO placeShip(Long boardId, Long playerId, int length, int x, int y, boolean isHorizontal) {
		Board board = boardRepository.findById(boardId).orElseThrow();
		Ship ship = board.placeShip(length, x, y, isHorizontal);
		boardRepository.save(board);
		ship = shipRepository.save(ship);

		return new ShipDTO(ship.getId(), ship.getLength(), playerId);
	}

	public String makeGuess(Long gameId, Long boardId, Long playerId, Long opponentId, int x, int y) {
		Board board = boardRepository.findById(boardId).orElseThrow();
		Cell cell = board.getCells().stream()
				.filter(c -> c.getX() == x && c.getY() == y)
				.findFirst().orElseThrow();

		boolean isHit = cell.isHasShip();
		cell.setHit(true);
		boardRepository.save(board);

		BoardMessage message = new BoardMessage();
		message.setGameId(gameId);
		message.setBoardId(boardId);
		message.setPlayerId(playerId);
		message.setOpponentId(opponentId);
		message.setX(x);
		message.setY(y);
		message.setHit(isHit);

		boardMessageSender.sendEvent(isHit ? "SHIP_HIT" : "SHOT_MISSED", message);

		if (board.isAllShipsHit()) {
			boardMessageSender.sendEvent("GAME_OVER", message);
		}

		return isHit ? "Hit!" : "Miss!";
	}

	public String endGame(Long boardId) {
		Board board = boardRepository.findById(boardId).orElseThrow();
		board.getCells().forEach(cell -> cell.setHit(true));
		boardRepository.save(board);
		return "Game ended successfully";
	}

	private BoardDTO convertToDTO(Board board) {
		List<CellDTO> cellDTOs = board.getCells().stream()
				.map(cell -> new CellDTO(cell.getX(), cell.getY(), cell.isHasShip(), cell.isHit()))
				.collect(Collectors.toList());

		return new BoardDTO(board.getId(), cellDTOs);
	}
}