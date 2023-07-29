package org.springframework.test.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ApplicationContextEvent;

public class CustomEvent extends ApplicationEvent {

    private Long id;

    private String message;
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public CustomEvent(Object source,Long id ,String message) {
        super(source);
        this.id = id;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public CustomEvent setId(Long id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CustomEvent setMessage(String message) {
        this.message = message;
        return this;
    }
}
