package sem4.ea.ss25.battleship.assignment2.game_service.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.game_service.config.RabbitMQConfig;
import sem4.ea.ss25.battleship.assignment2.game_service.domain.GameMessage;
import sem4.ea.ss25.battleship.assignment2.game_service.dto.GameDTO;

import java.io.IOException;

@Service
public class GameMessageReceiver {
	private final GameService gameService;
	private final GameMessageSender messageSender;

	public GameMessageReceiver(GameService gameService, GameMessageSender messageSender) {
		this.gameService = gameService;
		this.messageSender = messageSender;
	}

	@RabbitListener(queues = RabbitMQConfig.GAME_COMMANDS_QUEUE)
	public void handleCommand(GameMessage message, Channel channel,
							  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		try {
			switch (message.getType()) {
				case "CREATE_GAME":
					handleCreateGame(message);
					break;
				case "ADD_PLAYER":
					handleAddPlayer(message);
					break;
				default:
					throw new IllegalArgumentException("Unknown command type");
			}
			channel.basicAck(tag, false);
		} catch (Exception e) {
			channel.basicNack(tag, false, true);
		}
	}

	private void handleCreateGame(GameMessage message) {
		GameDTO game = gameService.createGame();
		GameMessage response = new GameMessage();
		response.setGameId(game.id());
		response.setBoardId(game.boardId());
		messageSender.sendEvent("GAME_CREATED", response);
	}

	private void handleAddPlayer(GameMessage message) {
		gameService.addPlayerToGame(message.getGameId(), message.getPlayerId());
		messageSender.sendEvent("PLAYER_ADDED", message);
	}
}