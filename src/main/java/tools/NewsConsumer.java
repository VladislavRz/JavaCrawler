package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import items.NewsItem;

import java.io.IOException;

public class NewsConsumer extends MsgConsumer {
    public NewsConsumer(String exchangeName, String queueName, String queueKey) {
        super(exchangeName, queueName, queueKey);
        logger.info("Start consume news");
    }

    @Override
    protected void consume(Channel channel, Envelope envelope, String body) throws IOException {

        // Обработка сообщения
        NewsItem news = new NewsItem(body);

        // Отправка в базу данных
        elastic.insertNote(news);

        // Удаление из очереди
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}
