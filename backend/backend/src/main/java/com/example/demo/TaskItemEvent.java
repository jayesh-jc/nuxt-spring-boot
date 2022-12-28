package com.example.demo;

import org.springframework.context.ApplicationEvent;

/**
 * Event thrown when CatalogueItem is created or updated
 */
public class TaskItemEvent extends ApplicationEvent {

    public static final String CATALOGUEITEM_CREATED = "CREATED";
    public static final String CATALOGUEITEM_UPDATED = "UPDATED";

    private String eventType;

    public TaskItemEvent(String eventType, Customer catalogueItem) {
        super(catalogueItem);
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}