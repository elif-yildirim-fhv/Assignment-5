package sem4.ea.ss25.battleship.assignment2.board_service.service;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import sem4.ea.ss25.battleship.assignment2.board_service.config.RabbitMQConfig;
import sem4.ea.ss25.battleship.assignment2.board_service.domain.BoardMessage;
import sem4.ea.ss25.battleship.assignment2.board_service.dto.BoardDTO;

import java.io.IOException;

@Service
public class BoardMessageReceiver {
	private final BoardService boardService;
	private final BoardMessageSender messageSender;

	public BoardMessageReceiver(BoardService boardService, BoardMessageSender messageSender) {
		this.boardService = boardService;
		this.messageSender = messageSender;
	}

	@RabbitListener(queues = RabbitMQConfig.BOARD_COMMANDS_QUEUE)
	public void handleCommand(BoardMessage message, Channel channel,
							  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
		try {
			switch (message.getType()) {
				case "CREATE_BOARD":
					handleCreateBoard(message);
					break;
				case "PLACE_SHIP":
					handlePlaceShip(message);
					break;
				default:
					throw new IllegalArgumentException("Unknown command type");
			}
			channel.basicAck(tag, false);
		} catch (Exception e) {
			channel.basicNack(tag, false, true);
		}
	}

	private void handleCreateBoard(BoardMessage message) {
		BoardDTO board = boardService.createBoard();
		BoardMessage response = new BoardMessage();
		response.setBoardId(board.id());
		messageSender.sendEvent("BOARD_CREATED", response);
	}

	private void handlePlaceShip(BoardMessage message) {
		boardService.placeShip(
				message.getBoardId(),
				message.getPlayerId(),
				message.getLength(),
				message.getX(),
				message.getY(),
				message.isHorizontal()
		);
		messageSender.sendEvent("SHIP_PLACED", message);
	}
}