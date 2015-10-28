package net.kimleo.mq;

import java.io.Serializable;
import java.util.Comparator;

@FunctionalInterface
public interface Message<T> extends Serializable, Comparable<Message> {

    T get();

    default int priority() {
        return 1;
    }

    static <T> Message<T> withPriority(int priority, Message<T> message) {
        return new Message<T>() {
            Message<T> innerMessage = message;
            @Override
            public T get() {
                return innerMessage.get();
            }

            @Override
            public int priority() {
                return priority;
            }
        };
    }

    @Override
    default int compareTo(Message message) {
        return message.priority() - this.priority();
    }
}
