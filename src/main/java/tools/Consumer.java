package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;


public abstract class Consumer extends Thread {
    private static final String MAIN_URL = "https://histrf.ru/read/articles";
    private static final String USER = "rabbitmq";
    private static final String PASS = "rabbitmq";
    private static final String VHOST = "/";
    private static final String LHOST = "127.0.0.1";
    private static final int PORT = 5672;
    protected final String exchangeName;
    protected final String queueKey;
    protected final String queueName;

    public Consumer(String exchangeName, String queueName, String queueKey) {
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.queueKey = queueKey;
    }

    @Override
    public void run() {
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(USER);
            factory.setPassword(PASS);
            factory.setVirtualHost(VHOST);
            factory.setHost(LHOST);
            factory.setPort(PORT);
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();

            channel.exchangeDeclare(exchangeName, "direct", durable);
            channel.queueDeclare(queueName, durable, false, false, null);
            channel.queueBind(queueName, exchangeName, queueKey);
            channel.basicConsume(queueName, false, this.consume);

            // TODO: Взятие сообщений из очереди

            channel.close();
            conn.close();
        }
        catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void consume(Channel channel);
}
