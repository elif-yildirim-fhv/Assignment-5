package sem4.ea.ss25.battleship.assignment2.player_service.domain;

import java.io.Serializable;

public class PlayerMessage implements Serializable {
	private String type;
	private Long playerId;
	private String playerName;
	private boolean isHit;

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public Long getPlayerId() { return playerId; }
	public void setPlayerId(Long playerId) { this.playerId = playerId; }
	public String getPlayerName() { return playerName; }
	public void setPlayerName(String playerName) { this.playerName = playerName; }
	public boolean isHit() { return isHit; }
	public void setHit(boolean hit) { isHit = hit; }
}