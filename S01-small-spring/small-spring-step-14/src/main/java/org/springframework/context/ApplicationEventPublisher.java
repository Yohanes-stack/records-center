package org.springframework.context;

public interface ApplicationEventPublisher {
    void publicEvent(ApplicationEvent event);
}
