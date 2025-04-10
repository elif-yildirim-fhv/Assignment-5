package sem4.ea.ss25.battleship.assignment2.board_service.domain;

import java.io.Serializable;

public class BoardMessage implements Serializable {
	private String type;
	private Long boardId;
	private Long gameId;
	private Long playerId;
	private Long opponentId;
	private int x;
	private int y;
	private int length;
	private boolean isHorizontal;
	private boolean isHit;

	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public Long getBoardId() { return boardId; }
	public void setBoardId(Long boardId) { this.boardId = boardId; }
	public Long getGameId() { return gameId; }
	public void setGameId(Long gameId) { this.gameId = gameId; }
	public Long getPlayerId() { return playerId; }
	public void setPlayerId(Long playerId) { this.playerId = playerId; }
	public Long getOpponentId() { return opponentId; }
	public void setOpponentId(Long opponentId) { this.opponentId = opponentId; }
	public int getX() { return x; }
	public void setX(int x) { this.x = x; }
	public int getY() { return y; }
	public void setY(int y) { this.y = y; }
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
	public boolean isHorizontal() { return isHorizontal; }
	public void setHorizontal(boolean horizontal) { isHorizontal = horizontal; }
	public boolean isHit() { return isHit; }
	public void setHit(boolean hit) { isHit = hit; }
}