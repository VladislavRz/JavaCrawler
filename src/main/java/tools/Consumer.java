package tools;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;


public class Consumer extends Thread {
//    public static void main(String[] args) throws Exception {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setUsername("rabbitmq");
//        factory.setPassword("rabbitmq");
//        factory.setVirtualHost("/");
//        factory.setHost("127.0.0.1");
//        factory.setPort(5672);
//        Connection conn = factory.newConnection();
//        Channel channel = conn.createChannel();
//        String exchangeName = "myExchange";
//        String queueName = "myQueue";
//        String routingKey = "testRoute";
//        boolean durable = true;
//        channel.exchangeDeclare(exchangeName, "direct", durable);
//        channel.queueDeclare(queueName, durable, false, false, null);
//        channel.queueBind(queueName, exchangeName, routingKey);
//        QueueingConsumer consumer = new QueueingConsumer(channel);
//        channel.basicConsume(queueName, false, consumer);
//        boolean run = true;
//        while (run) {
//            QueueingConsumer.Delivery delivery;
//            try {
//                delivery = consumer.nextDelivery();
//                new MessageThread(channel, new String(delivery.getBody()), delivery.getEnvelope().getDeliveryTag()).start();
//            } catch (InterruptedException ie) {
//                continue;
//            }
//        }
//        channel.close();
//        conn.close();
//    }

    private Channel channel;
    private String message;
    private long tag;

//    public MessageThread(Channel channel, String message, long tag) {
//        this.channel = channel;
//        this.message = message;
//        this.tag = tag;
//    }

    @Override
    public void run() {
        try {
            System.err.println("Message received " + message);
            sleep(5000); // имитируем обработку сообщения
            channel.basicAck(tag, false);//подтверждаете получение сообщения
            System.err.println("Message deleted " + message);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
