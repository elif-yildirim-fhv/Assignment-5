package sem4.ea.ss25.battleship.assignment2.game_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "board-service")
public interface BoardServiceClient {

	@PostMapping("/api/board/create")
	Long createBoard();

	// ... existing code ...
} 