package sem4.ea.ss25.battleship.assignment2.api_gateway.dto;

public record CellDTO(
    int x,
    int y,
    boolean hasShip,
    boolean isHit
) {}
