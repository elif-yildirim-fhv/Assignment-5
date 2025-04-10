package sem4.ea.ss25.battleship.assignment2.board_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	public static final String EXCHANGE = "battleship.direct";
	public static final String BOARD_COMMANDS_QUEUE = "board.commands.queue";
	public static final String BOARD_EVENTS_QUEUE = "board.events.queue";
	public static final String BOARD_COMMANDS_ROUTING_KEY = "board.commands";
	public static final String BOARD_EVENTS_ROUTING_KEY = "board.events";

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue boardCommandsQueue() {
		return QueueBuilder.durable(BOARD_COMMANDS_QUEUE)
				.withArgument("x-dead-letter-exchange", "")
				.withArgument("x-dead-letter-routing-key", "board.commands.dlq")
				.build();
	}

	@Bean
	public Queue boardEventsQueue() {
		return new Queue(BOARD_EVENTS_QUEUE, true);
	}

	@Bean
	public Binding boardCommandsBinding() {
		return BindingBuilder.bind(boardCommandsQueue())
				.to(exchange())
				.with(BOARD_COMMANDS_ROUTING_KEY);
	}

	@Bean
	public Binding boardEventsBinding() {
		return BindingBuilder.bind(boardEventsQueue())
				.to(exchange())
				.with(BOARD_EVENTS_ROUTING_KEY);
	}
}