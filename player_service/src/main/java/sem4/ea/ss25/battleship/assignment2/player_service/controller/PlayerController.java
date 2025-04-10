package sem4.ea.ss25.battleship.assignment2.player_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem4.ea.ss25.battleship.assignment2.player_service.domain.PlayerMessage;
import sem4.ea.ss25.battleship.assignment2.player_service.service.PlayerMessageSender;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

	@Autowired
	private PlayerMessageSender playerMessageSender;

	@PostMapping("/create")
	public ResponseEntity<String> createPlayer(@RequestParam String name) {
		PlayerMessage message = new PlayerMessage();
		message.setPlayerName(name);
		playerMessageSender.sendCommand("CREATE_PLAYER", message);
		return ResponseEntity.accepted().body("Player creation request sent");
	}

	@GetMapping("/{playerId}")
	public ResponseEntity<String> getPlayer(@PathVariable Long playerId) {
		PlayerMessage message = new PlayerMessage();
		message.setPlayerId(playerId);
		playerMessageSender.sendCommand("GET_PLAYER", message);
		return ResponseEntity.accepted().body("Player data request sent");
	}

	@PostMapping("/{playerId}/updateScore")
	public ResponseEntity<String> updateScore(
			@PathVariable Long playerId,
			@RequestParam boolean isHit) {

		PlayerMessage message = new PlayerMessage();
		message.setPlayerId(playerId);
		message.setHit(isHit);

		playerMessageSender.sendCommand("UPDATE_SCORE", message);
		return ResponseEntity.accepted().body("Score update request sent");
	}
}