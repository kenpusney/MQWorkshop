package net.kimleo.mq;

public interface Observable<T extends Observer> {
    void bind(T observer);
    void detach(T observer);
}
