package sem4.ea.ss25.battleship.assignment2.api_gateway.dto;

import java.util.List;

public record GameDetailsDTO(
    Long gameId,
    Long boardId,
    List<PlayerDTO> players,
    BoardDTO board
) {}
