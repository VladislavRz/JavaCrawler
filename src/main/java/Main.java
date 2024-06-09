import org.jsoup.nodes.Document;
import tools.MsgProducer;
import tools.NewsConsumer;
import tools.UrlConsumer;
import webwork.Requester;

import java.io.IOException;

public class Main {
    private static final String exchangeName = "CrawlerExchange";
    private static final String urlQueueName = "UrlQueue";
    private static final String newsQueueName = "NewsQueue";
    private static final String urlQueueKey = "UrlQueueKey";
    private static final String newsQueueKey = "NewsQueueKey";

    public static void main(String[] args) {
        MsgProducer producer = new MsgProducer(exchangeName, urlQueueKey);
        UrlConsumer urlConsumer = new UrlConsumer(exchangeName, urlQueueName, urlQueueKey, newsQueueKey);
        NewsConsumer newsConsumer = new NewsConsumer(exchangeName, newsQueueName, newsQueueKey);

        try {
            producer.start();
            urlConsumer.start();
            newsConsumer.start();

            // Ожидание окончания выполнения потоков
            producer.join();
            urlConsumer.join();
            newsConsumer.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}