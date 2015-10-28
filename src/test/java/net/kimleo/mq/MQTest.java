package net.kimleo.mq;

import net.kimleo.mq.impl.MQImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static net.kimleo.mq.Message.withPriority;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class MQTest {

    MQ mq = new MQImpl();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        mq.shutdown();
    }

    @Test
    public void testProducerAndConsumer() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumer = mq.createConsumer();

        Queue queue = mq.createQueue("hello");

        producer.send(()-> "hello", queue);

        consumer.acceptOnce(queue, message -> assertThat(message.get(), is("hello")));
    }

    @Test
    public void testShouldInvokeHandler() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumer = mq.createConsumer();

        Queue queue = mq.createQueue("hello");

        Message message = mock(Message.class);
        when(message.get()).thenReturn("hello");

        producer.send(message, queue);

        consumer.acceptOnce(queue, msg -> assertThat(msg.get(), is("hello")));

        verify(message, atLeastOnce()).get();
    }

    @Test
    public void testFIFO() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumer = mq.createConsumer();

        Queue queue = mq.createQueue("order");

        producer.send(() -> "first", queue);
        producer.send(() -> "second", queue);

        consumer.listen(queue, message -> assertThat(message.get(), is("first")));
        consumer.listen(queue, message -> assertThat(message.get(), is("second")));
    }

    @Test
    public void testOnce() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumer = mq.createConsumer();

        Queue queue = mq.createQueue("once");
        Message msg = mock(Message.class);
        when(msg.get()).thenReturn("once");

        producer.send(msg, queue);
        producer.send(msg, queue);

        consumer.acceptOnce(queue, message -> assertThat(message.get(), is("once")));

        producer.send(msg, queue);

        verify(msg, times(1)).get();
    }

    @Test
    public void testPriority() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumer = mq.createConsumer();

        Queue queue = mq.createQueue("priority");

        Message<String> messageWithDefaultPriority = () -> "default priority";
        Message<String> messageWithHighPriority = withPriority(10, () -> "high priority");

        producer.send(messageWithDefaultPriority, queue);
        producer.send(messageWithHighPriority, queue);

        consumer.acceptOnce(queue, message -> assertThat(message.get(), is("high priority")));
        consumer.acceptOnce(queue, message -> assertThat(message.get(), is("default priority")));
    }

    @Test
    public void testTopicWithMultipleConsumer() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumer = mq.createConsumer();
        Consumer anotherConsumer = mq.createConsumer();

        Topic topic = mq.createTopic("hello");

        Message msg = mock(Message.class);
        when(msg.get()).thenReturn("hello");

        producer.send(msg, topic);

        consumer.listen(topic, message -> assertThat(message.get(), is("hello")));
        anotherConsumer.listen(topic, message -> assertThat(message.get(), is("hello")));

        producer.send(msg, topic);

        verify(msg, times(2)).get();
    }

    @Test
    public void testAsync() throws Exception {
        Consumer consumer = mq.createConsumer();
        Producer producer = mq.createProducer();

        Queue queue = mq.createQueue("async");

        Message msg = mock(Message.class);
        when(msg.get()).thenReturn("async message");

        consumer.listen(queue, message -> {
            assertThat(message.get(), is("async message"));
        });

        producer.send(msg, queue);
        producer.send(msg, queue);
        producer.send(msg, queue);

        verify(msg, times(3)).get();
    }

    @Test
    public void testChat() throws Exception {
        Producer producer = mq.createProducer();
        Consumer consumerA = mq.createConsumer();
        Consumer consumerB = mq.createConsumer();
        CallSite callSite = mock(CallSite.class);

        Queue mailBoxA = mq.createQueue("mailbox-a");
        Queue mailBoxB = mq.createQueue("mailbox-b");

        consumerA.listen(mailBoxA, message -> {
            String text = (String) message.get();
            if(text.length() < 50) {
                producer.send(() -> text+" then to A", mailBoxB);
            } else {
                assertThat(text, is("hello from test then to A then to B then to A then to B"));
            }
        });
        consumerB.listen(mailBoxB, message -> {
            String text = (String) message.get();
            callSite.invoke();
            producer.send(() -> text+ " then to B", mailBoxA);
        });

        producer.send(()->"hello from test", mailBoxA);

        verify(callSite, times(2)).invoke();
    }
}