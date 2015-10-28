package net.kimleo.mq.impl;

import net.kimleo.mq.Consumer;
import net.kimleo.mq.Destination;
import net.kimleo.mq.Handler;
import net.kimleo.mq.Message;

public class ConsumerImpl implements Consumer {

    public Handler handler;
    private boolean onlyOnce = false;

    @Override
    public <T> void acceptOnce(Destination destination, Handler<Message<T>> handler) {
        this.handler = handler;
        this.onlyOnce = true;
        destination.bind(this);
    }

    @Override
    public <T> void listen(Destination destination, Handler<Message<T>> handler) {
        this.handler = handler;
        destination.bind(this);
    }

    @Override
    public void update(Destination destination, Message message) {
        handler.handle(message);
        if(onlyOnce) {
            destination.detach(this);
        }
    }

}
