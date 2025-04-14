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
import sem4.ea.ss25.battleship.assignment2.board_service.dto.GuessResultDTO;
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

	public BoardDTO getBoard(Long boardId) {
		Board board = boardRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("Board not found with id: " + boardId));
		return convertToDTO(board);
	}

	public BoardDTO createBoard() {
		Board board = new Board();
		board.initializeBoard(10);
		board = boardRepository.save(board);

		return convertToDTO(board);
	}

	public BoardDTO placeShip(Long boardId, Long playerId, int length, int x, int y, boolean isHorizontal) {
		Board board = boardRepository.findById(boardId).orElseThrow();
		Ship ship = board.placeShip(length, x, y, isHorizontal, playerId);
		boardRepository.save(board);
		ship = shipRepository.save(ship);

		return convertToDTO(board);
	}

	public GuessResultDTO makeGuess(Long gameId, Long boardId, Long playerId, Long opponentId, int x, int y) {
		Board board = boardRepository.findById(boardId).orElseThrow();
		Cell cell = board.getCells().stream()
				.filter(c -> c.getX() == x && c.getY() == y)
				.findFirst()
				.orElseThrow();

		boolean isHit = cell.isOccupied();
		cell.setGuessed(true);
		boardRepository.save(board);

		// Check if all ships are sunk
		boolean isGameOver = board.getShips().stream()
				.allMatch(ship -> ship.getCells().stream()
						.allMatch(Cell::isGuessed));

		return new GuessResultDTO(
			boardId,
			playerId,
			opponentId,
			x,
			y,
			isHit,
			isGameOver
		);
	}

	public BoardDTO endGame(Long boardId) {
		Board board = boardRepository.findById(boardId).orElseThrow();
		board.getCells().forEach(cell -> cell.setGuessed(true));
		board = boardRepository.save(board);
		return convertToDTO(board);
	}

	private BoardDTO convertToDTO(Board board) {
		List<CellDTO> cellDTOs = board.getCells().stream()
				.map(cell -> new CellDTO(
					cell.getX(),
					cell.getY(),
					cell.isOccupied(),
					cell.isGuessed()
				))
				.collect(Collectors.toList());

		List<ShipDTO> shipDTOs = board.getShips().stream()
				.map(ship -> new ShipDTO(
					ship.getId(),
					ship.getLength(),
					ship.getPlayerId()
				))
				.collect(Collectors.toList());

		return new BoardDTO(
			board.getId(),
			cellDTOs,
			shipDTOs
		);
	}
}