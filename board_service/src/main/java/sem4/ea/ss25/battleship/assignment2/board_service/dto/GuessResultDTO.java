package sem4.ea.ss25.battleship.assignment2.board_service.dto;

public record GuessResultDTO(
    Long boardId,
    Long playerId,
    Long opponentId,
    int x,
    int y,
    boolean isHit,
    boolean isGameOver
) {} 