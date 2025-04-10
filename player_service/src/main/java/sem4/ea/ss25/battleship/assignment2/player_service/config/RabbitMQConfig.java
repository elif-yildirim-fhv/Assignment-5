package sem4.ea.ss25.battleship.assignment2.player_service.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	public static final String EXCHANGE = "battleship.direct";
	public static final String PLAYER_COMMANDS_QUEUE = "player.commands.queue";
	public static final String PLAYER_EVENTS_QUEUE = "player.events.queue";
	public static final String BOARD_EVENTS_QUEUE = "board.events.queue";
	public static final String GAME_EVENTS_QUEUE = "game.events.queue";
	public static final String PLAYER_COMMANDS_ROUTING_KEY = "player.commands";
	public static final String PLAYER_EVENTS_ROUTING_KEY = "player.events";
	public static final String BOARD_EVENTS_ROUTING_KEY = "board.events";
	public static final String GAME_EVENTS_ROUTING_KEY = "game.events";

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue playerCommandsQueue() {
		return QueueBuilder.durable(PLAYER_COMMANDS_QUEUE)
				.withArgument("x-dead-letter-exchange", "")
				.withArgument("x-dead-letter-routing-key", "player.commands.dlq")
				.build();
	}

	@Bean
	public Queue playerEventsQueue() {
		return new Queue(PLAYER_EVENTS_QUEUE, true);
	}

	@Bean
	public Queue boardEventsQueue() {
		return new Queue(BOARD_EVENTS_QUEUE, true);
	}

	@Bean
	public Queue gameEventsQueue() {
		return new Queue(GAME_EVENTS_QUEUE, true);
	}

	@Bean
	public Binding playerCommandsBinding() {
		return BindingBuilder.bind(playerCommandsQueue())
				.to(exchange())
				.with(PLAYER_COMMANDS_ROUTING_KEY);
	}

	@Bean
	public Binding playerEventsBinding() {
		return BindingBuilder.bind(playerEventsQueue())
				.to(exchange())
				.with(PLAYER_EVENTS_ROUTING_KEY);
	}

	@Bean
	public Binding boardEventsBinding() {
		return BindingBuilder.bind(boardEventsQueue())
				.to(exchange())
				.with(BOARD_EVENTS_ROUTING_KEY);
	}

	@Bean
	public Binding gameEventsBinding() {
		return BindingBuilder.bind(gameEventsQueue())
				.to(exchange())
				.with(GAME_EVENTS_ROUTING_KEY);
	}
}