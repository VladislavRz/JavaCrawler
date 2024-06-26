import tools.ElasticClient;
import tools.MsgProducer;
import tools.NewsConsumer;
import tools.UrlConsumer;

public class Main {
    private static final String exchangeName = "CrawlerExchange";
    private static final String urlQueueName = "UrlQueue";
    private static final String newsQueueName = "NewsQueue";
    private static final String urlQueueKey = "UrlQueueKey";
    private static final String newsQueueKey = "NewsQueueKey";

    public static void main(String[] args) {

        // Создание клиента базы данных
        ElasticClient elastic = ElasticClient.getInstance();

        // Создание индекса в базе данных
        elastic.createIndex();

        // Создание обработчиков
        MsgProducer producer = new MsgProducer(exchangeName, urlQueueKey);
        UrlConsumer urlConsumer = new UrlConsumer(exchangeName, urlQueueName, urlQueueKey, newsQueueKey);
        NewsConsumer newsConsumer = new NewsConsumer(exchangeName, newsQueueName, newsQueueKey);

        try {
            // Запуск потоков с обработкой страниц
            producer.start();
            urlConsumer.start();
            newsConsumer.start();

            Thread.sleep(10000);

            // Поиск значений в базе
            elastic.searchNote();
            elastic.buildAggregations();

            // Ожидание окончания выполнения потоков
            producer.join();
            urlConsumer.join();
            newsConsumer.join();

        } catch (InterruptedException | RuntimeException e) {
            e.printStackTrace();
        }
    }
}