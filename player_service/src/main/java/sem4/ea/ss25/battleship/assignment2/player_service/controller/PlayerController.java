package sem4.ea.ss25.battleship.assignment2.player_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sem4.ea.ss25.battleship.assignment2.player_service.domain.PlayerMessage;
import sem4.ea.ss25.battleship.assignment2.player_service.service.PlayerMessageSender;
import sem4.ea.ss25.battleship.assignment2.player_service.service.PlayerService;
import sem4.ea.ss25.battleship.assignment2.player_service.dto.PlayerDTO;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

	@Autowired
	private PlayerMessageSender playerMessageSender;

	@Autowired
	private PlayerService playerService;

	@PostMapping("/create")
	public ResponseEntity<Long> createPlayer(@RequestParam String name) {
		PlayerMessage message = new PlayerMessage();
		message.setPlayerName(name);
		playerMessageSender.sendCommand("CREATE_PLAYER", message);
		PlayerDTO player = playerService.createPlayer(name);
		return ResponseEntity.ok(player.id());
	}

	@GetMapping("/{playerId}")
	public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long playerId) {
		PlayerMessage message = new PlayerMessage();
		message.setPlayerId(playerId);
		playerMessageSender.sendCommand("GET_PLAYER", message);
		PlayerDTO player = playerService.getPlayer(playerId);
		return ResponseEntity.ok(player);
	}

	@PostMapping("/{playerId}/updateScore")
	public ResponseEntity<PlayerDTO> updateScore(
			@PathVariable Long playerId,
			@RequestParam boolean isHit) {

		PlayerMessage message = new PlayerMessage();
		message.setPlayerId(playerId);
		message.setHit(isHit);

		playerMessageSender.sendCommand("UPDATE_SCORE", message);
		PlayerDTO player = playerService.updateScore(playerId, isHit);
		return ResponseEntity.ok(player);
	}
}