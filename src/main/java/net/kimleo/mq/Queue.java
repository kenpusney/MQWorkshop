package net.kimleo.mq;

public interface Queue extends Destination {
    <T> Message get();
}
