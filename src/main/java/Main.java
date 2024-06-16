import tools.ElasticClient;
import tools.MsgProducer;
import tools.NewsConsumer;
import tools.UrlConsumer;

import java.io.IOException;

public class Main {
    private static final String exchangeName = "CrawlerExchange";
    private static final String urlQueueName = "UrlQueue";
    private static final String newsQueueName = "NewsQueue";
    private static final String urlQueueKey = "UrlQueueKey";
    private static final String newsQueueKey = "NewsQueueKey";

    public static void main(String[] args) {
        // Создание клиента базы данных
        ElasticClient elastic = ElasticClient.getInstance();

        // Создание индекса
        elastic.createIndex();

        //Создание обработчиков
        MsgProducer producer = new MsgProducer(exchangeName, urlQueueKey);
        UrlConsumer urlConsumer = new UrlConsumer(exchangeName, urlQueueName, urlQueueKey, newsQueueKey);
        NewsConsumer newsConsumer = new NewsConsumer(exchangeName, newsQueueName, newsQueueKey);

        try {
            // Запуск потоков с обработкой страниц
            producer.start();
            urlConsumer.start();
            newsConsumer.start();

            // Ожидание окончания выполнения потоков
            producer.join();
            urlConsumer.join();
            newsConsumer.join();

            // Поиск значений в базе
            elastic.searchNote();

        } catch (InterruptedException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}