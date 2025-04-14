package sem4.ea.ss25.battleship.assignment2.board_service.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Cell> cells = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Ship> ships = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public void setCells(List<Cell> cells) {
		this.cells = cells;
	}

	public List<Ship> getShips() {
		return ships;
	}

	public void setShips(List<Ship> ships) {
		this.ships = ships;
	}

	public void initializeBoard(int size) {
		cells.clear();
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Cell cell = new Cell();
				cell.setX(x);
				cell.setY(y);
				cell.setOccupied(false);
				cell.setGuessed(false);
				cells.add(cell);
			}
		}
	}

	public Ship placeShip(int length, int x, int y, boolean isHorizontal, Long playerId) {
		if (!isValidPlacement(length, x, y, isHorizontal)) {
			throw new IllegalArgumentException("Invalid ship placement");
		}

		Ship ship = new Ship();
		ship.setLength(length);
		ship.setPlayerId(playerId);

		if (isHorizontal) {
			for (int i = 0; i < length; i++) {
				Cell cell = getCellAt(x + i, y);
				cell.setOccupied(true);
				ship.getCells().add(cell);
			}
		} else {
			for (int i = 0; i < length; i++) {
				Cell cell = getCellAt(x, y + i);
				cell.setOccupied(true);
				ship.getCells().add(cell);
			}
		}

		ships.add(ship);
		return ship;
	}

	private boolean isValidPlacement(int length, int x, int y, boolean isHorizontal) {
		if (x < 0 || y < 0) {
			return false;
		}

		if (isHorizontal) {
			if (x + length > 10) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				Cell cell = getCellAt(x + i, y);
				if (cell == null || cell.isOccupied()) {
					return false;
				}
			}
		} else {
			if (y + length > 10) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				Cell cell = getCellAt(x, y + i);
				if (cell == null || cell.isOccupied()) {
					return false;
				}
			}
		}

		return true;
	}

	private Cell getCellAt(int x, int y) {
		return cells.stream()
				.filter(cell -> cell.getX() == x && cell.getY() == y)
				.findFirst()
				.orElse(null);
	}

	public boolean isAllShipsHit() {
		return ships.stream()
				.allMatch(Ship::isDestroyed);
	}
}