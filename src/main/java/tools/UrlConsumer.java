package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import items.NewsItem;
import items.UrlItem;
import org.jsoup.nodes.Document;
import webwork.Parser;
import webwork.Requester;

import java.io.IOException;

public class UrlConsumer extends MsgConsumer {
    private final String newsQueuekey;

    public UrlConsumer(String exchangeName, String queueName, String queueKey, String newsQueueKey) {
        super(exchangeName, queueName, queueKey);
        this.newsQueuekey = newsQueueKey;
    }

    @Override
    protected void consume(Channel channel, Envelope envelope, String body) throws IOException {

        System.out.println("Start Url Consumer");

        // Парсинг страницы
        long deliveryTag = envelope.getDeliveryTag();
        UrlItem url = new UrlItem(body);
        Document doc = Requester.getRequest(url.getUrl());
        NewsItem news = Parser.parseNews(url, doc);

        // Отправка в новую очередь
        channel.basicPublish(exchangeName,
                newsQueuekey,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                news.obj2json().getBytes());

        // Удаление сообщения из очереди
        channel.basicAck(deliveryTag, false);
        System.out.println("Finish Url Consumer");
    }
}
