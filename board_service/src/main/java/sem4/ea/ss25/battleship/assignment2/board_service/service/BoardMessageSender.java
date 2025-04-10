package sem4.ea.ss25.battleship.assignment2.board_service.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.board_service.config.RabbitMQConfig;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.BoardMessage;

@Service
public class BoardMessageSender {
	private final RabbitTemplate rabbitTemplate;

	public BoardMessageSender(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendCommand(String commandType, BoardMessage message) {
		message.setType(commandType);
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.BOARD_COMMANDS_ROUTING_KEY,
				message
		);
	}

	public void sendEvent(String eventType, BoardMessage message) {
		message.setType(eventType);
		rabbitTemplate.convertAndSend(
				RabbitMQConfig.EXCHANGE,
				RabbitMQConfig.BOARD_EVENTS_ROUTING_KEY,
				message
		);
	}
}