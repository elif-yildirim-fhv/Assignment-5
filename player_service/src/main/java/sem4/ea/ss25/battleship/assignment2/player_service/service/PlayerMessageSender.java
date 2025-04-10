package sem4.ea.ss25.battleship.assignment2.player_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.player_service.config.RabbitMQConfig;
import sem4.ea.ss25.battleship.assignment2.player_service.domain.PlayerMessage;

@Service
public class PlayerMessageSender {
	private final RabbitTemplate rabbitTemplate;

	public PlayerMessageSender(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendCommand(String commandType, PlayerMessage message) {
		message.setType(commandType);
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.PLAYER_COMMANDS_ROUTING_KEY,
				message
		);
	}

	public void sendEvent(String eventType, PlayerMessage message) {
		message.setType(eventType);
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.PLAYER_EVENTS_ROUTING_KEY,
				message
		);
	}
}