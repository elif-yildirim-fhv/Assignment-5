package sem4.ea.ss25.battleship.assignment2.game_service.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GAME")
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long boardId;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "GAME_PLAYER_IDS", 
		joinColumns = @JoinColumn(name = "GAME_ID"))
	@Column(name = "PLAYER_IDS")
	private List<Long> playerIds = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public List<Long> getPlayerIds() {
		return playerIds;
	}

	public void addPlayerId(Long playerId) {
		if (this.playerIds == null) {
			this.playerIds = new ArrayList<>();
		}
		this.playerIds.add(playerId);
	}
}
