package tools;

import com.rabbitmq.client.*;
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

        // Парсинг страницы
        UrlItem url = new UrlItem(body);
        Document doc = Requester.getRequest(url.getUrl());
        NewsItem news = Parser.parseNews(url, doc);

        // Удаление из очереди
        channel.basicAck(envelope.getDeliveryTag(), false);

        // Отправка в новую очередь
        channel.basicPublish(exchangeName,
                newsQueuekey,
                null,
                news.obj2json().getBytes());
    }
}
