package tools;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


public abstract class MsgConsumer extends Thread {
    private static final String USER = "rabbitmq";
    private static final String PASS = "rabbitmq";
    private static final String VHOST = "/";
    private static final String LHOST = "127.0.0.1";
    private static final int PORT = 5672;

    protected final String exchangeName;
    protected final String queueKey;
    protected final String queueName;


    public MsgConsumer(String exchangeName, String queueName, String queueKey) {
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.queueKey = queueKey;
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

            // Создание очереди
            channel.exchangeDeclare(exchangeName, "direct", true);
            channel.queueDeclare(queueName,
                              true,         // durable
                                 false,        // exclusive
                                 false,        // autoDelete
                                 null          // arguments
            );
            channel.queueBind(queueName, exchangeName, queueKey);

            // Обработка сообщения
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    consume(channel, envelope, new String(body, StandardCharsets.UTF_8));
                }
            };

            while (true) {
                if (channel.messageCount(queueName) == 0) continue;
                channel.basicConsume(queueName, false, consumer);
            }

//            channel.close();
//            conn.close();
        }
        catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void consume(Channel channel, Envelope envelope, String body) throws IOException;
}
