package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import items.NewsItem;

import java.io.IOException;

public class NewsConsumer extends MsgConsumer {
    public NewsConsumer(String exchangeName, String queueName, String queueKey) {
        super(exchangeName, queueName, queueKey);
    }

    @Override
    protected void consume(Channel channel, Envelope envelope, String body) throws IOException {

        // Обработка сообщения
        long deliveryTag = envelope.getDeliveryTag();
        NewsItem news = new NewsItem(body);

        // Вывод новости
        System.out.println(news);

        // Удаление сообщения из очереди
        channel.basicAck(deliveryTag, false);
    }
}
