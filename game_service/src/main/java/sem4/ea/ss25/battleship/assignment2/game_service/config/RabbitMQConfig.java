package sem4.ea.ss25.battleship.assignment2.game_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	public static final String EXCHANGE = "battleship.direct";
	public static final String GAME_COMMANDS_QUEUE = "game.commands.queue";
	public static final String GAME_EVENTS_QUEUE = "game.events.queue";
	public static final String BOARD_EVENTS_QUEUE = "board.events.queue";
	public static final String GAME_COMMANDS_ROUTING_KEY = "game.commands";
	public static final String GAME_EVENTS_ROUTING_KEY = "game.events";
	public static final String BOARD_EVENTS_ROUTING_KEY = "board.events";

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue gameCommandsQueue() {
		return QueueBuilder.durable(GAME_COMMANDS_QUEUE)
				.withArgument("x-dead-letter-exchange", "")
				.withArgument("x-dead-letter-routing-key", "game.commands.dlq")
				.build();
	}

	@Bean
	public Queue gameEventsQueue() {
		return new Queue(GAME_EVENTS_QUEUE, true);
	}

	@Bean
	public Queue boardEventsQueue() {
		return new Queue(BOARD_EVENTS_QUEUE, true);
	}

	@Bean
	public Binding gameCommandsBinding() {
		return BindingBuilder.bind(gameCommandsQueue())
				.to(exchange())
				.with(GAME_COMMANDS_ROUTING_KEY);
	}

	@Bean
	public Binding gameEventsBinding() {
		return BindingBuilder.bind(gameEventsQueue())
				.to(exchange())
				.with(GAME_EVENTS_ROUTING_KEY);
	}

	@Bean
	public Binding boardEventsBinding() {
		return BindingBuilder.bind(boardEventsQueue())
				.to(exchange())
				.with(BOARD_EVENTS_ROUTING_KEY);
	}
}