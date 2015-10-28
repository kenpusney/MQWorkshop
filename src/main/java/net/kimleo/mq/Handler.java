package net.kimleo.mq;

@FunctionalInterface
public interface Handler<T> {
    void handle(T value);
}
