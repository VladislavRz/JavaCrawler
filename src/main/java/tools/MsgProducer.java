package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import items.UrlItem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webwork.Parser;
import webwork.Requester;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class MsgProducer extends Thread {
    private static final String MAIN_URL = "https://histrf.ru/read/articles";
    private static final String USER = "rabbitmq";
    private static final String PASS = "rabbitmq";
    private static final String VHOST = "/";
    private static final String LHOST = "127.0.0.1";
    private static final int PORT = 5672;

    private final String exchangeName;
    private final String queueKey;
    private final ElasticClient elastic;
    private static final Logger logger = LoggerFactory.getLogger(MsgProducer.class);


    public MsgProducer(String exchangeName, String queueKey) {
        this.exchangeName = exchangeName;
        this.queueKey = queueKey;
        this.elastic = ElasticClient.getInstance();

        logger.info("Start Produce messages");
    }

    @Override
    public void run() {
        try{
            // Установка соединения
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(USER);
            factory.setPassword(PASS);
            factory.setVirtualHost(VHOST);
            factory.setHost(LHOST);
            factory.setPort(PORT);
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();

            // Запись в очередь
            produce(channel);

            // Закрытие соединения
            channel.close();
            conn.close();
        }
        catch (IOException | TimeoutException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void produce(Channel channel) throws IOException, NoSuchAlgorithmException {

        // Парсинг главной страницы
        UrlItem item;
        Document main_page = Requester.getRequest(MAIN_URL);

        if (main_page == null) {
            logger.error("Can not get main page and exit");
            System.exit(1);
        }

        Elements articles = Parser.parseMain(main_page);

        // Отправка в очередь
        for(Element article : articles) {
            item = Parser.parseNote(article);

            if (elastic.checkNote(item)) continue;

            channel.basicPublish(exchangeName,
                                 queueKey,
                                 null,
                                 item.obj2json().getBytes());
        }
    }
}
