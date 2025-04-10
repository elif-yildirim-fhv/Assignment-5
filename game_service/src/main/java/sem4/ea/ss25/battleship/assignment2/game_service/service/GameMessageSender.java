package sem4.ea.ss25.battleship.assignment2.game_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.game_service.config.RabbitMQConfig;
import sem4.ea.ss25.battleship.assignment2.game_service.domain.GameMessage;

@Service
public class GameMessageSender {
	private final RabbitTemplate rabbitTemplate;

	public GameMessageSender(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendCommand(String commandType, GameMessage message) {
		message.setType(commandType);
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.GAME_COMMANDS_ROUTING_KEY,
				message
		);
	}

	public void sendEvent(String eventType, GameMessage message) {
		message.setType(eventType);
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.GAME_EVENTS_ROUTING_KEY,
				message
		);
	}
}