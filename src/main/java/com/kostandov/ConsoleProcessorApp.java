package com.kostandov;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ConsoleProcessorApp {
    public static final String ORDERS_QUEUE = "ordersQueue";
    public static final String ORDERS_RESULTS_EXCHANGER = "ordersResultsExchanger";
    private static final String ORDER_READY="Order ready:";
    private static final String NEW_ORDER="New order:";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        System.out.println(" [*] Waiting for tasks");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received task '" + message + "'");
            int id=checkMessage(message);
            channel.basicPublish(ORDERS_RESULTS_EXCHANGER, "", null, (ORDER_READY+id).getBytes());
        };

        channel.basicConsume(ORDERS_QUEUE, true, deliverCallback, consumerTag -> {
        });
    }
    public static int checkMessage(String message){

        try {
            String id=message.substring(message.indexOf(NEW_ORDER)+NEW_ORDER.length());
            Integer res=Integer.parseInt(id);
            return res;
        }catch (Exception e){
            throw new RuntimeException("Illegal Command!");
        }
    }
}