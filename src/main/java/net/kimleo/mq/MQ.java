package net.kimleo.mq;

public interface MQ {
    Producer createProducer();

    Consumer createConsumer();

    Queue createQueue(String id);

    Topic createTopic(String id);

    void shutdown();
}
