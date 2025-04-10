package sem4.ea.ss25.battleship.assignment2.player_service.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.player_service.config.RabbitMQConfig;
import sem4.ea.ss25.battleship.assignment2.player_service.domain.PlayerMessage;

import java.io.IOException;

@Service
public class PlayerMessageReceiver {
	private final PlayerService playerService;
	private final PlayerMessageSender messageSender;

	public PlayerMessageReceiver(PlayerService playerService, PlayerMessageSender messageSender) {
		this.playerService = playerService;
		this.messageSender = messageSender;
	}

	@RabbitListener(queues = RabbitMQConfig.PLAYER_COMMANDS_QUEUE)
	public void handleCommand(PlayerMessage message, Channel channel,
							  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		try {
			switch (message.getType()) {
				case "CREATE_PLAYER":
					handleCreatePlayer(message);
					break;
				case "UPDATE_SCORE":
					handleUpdateScore(message);
					break;
				default:
					throw new IllegalArgumentException("Unknown command type");
			}
			channel.basicAck(tag, false);
		} catch (Exception e) {
			channel.basicNack(tag, false, true);
		}
	}

	private void handleCreatePlayer(PlayerMessage message) {
		playerService.createPlayer(message.getPlayerName());
		messageSender.sendEvent("PLAYER_CREATED", message);
	}

	private void handleUpdateScore(PlayerMessage message) {
		playerService.updateScore(message.getPlayerId(), message.isHit());
		messageSender.sendEvent("SCORE_UPDATED", message);
	}
}