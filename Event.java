package com.example.kowshick.travelmate;

import java.io.Serializable;

public class Event implements Serializable {
    private String id;
    private String eventName;
    private String startLocation;
    private String destination;
    private String departure;
    private String createDate;
    private double budget;

    public Event(String id, String eventName, String startLocation, String destination, String departure,String createDate, double budget) {
        this.id = id;
        this.eventName = eventName;
        this.startLocation = startLocation;
        this.destination = destination;
        this.departure = departure;
        this.budget = budget;
        this.createDate=createDate;
    }

    public Event() {
    }

    public Event(String eventName, String startLocation, String destination, String departure, String createDate, double budget) {
        this.eventName = eventName;
        this.startLocation = startLocation;
        this.destination = destination;
        this.departure = departure;
        this.budget = budget;
        this.createDate=createDate;
    }

    public String getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getDestination() {
        return destination;
    }

    public String getDeparture() {
        return departure;
    }
    public String getCreateDate() {
        return createDate;
    }

    public double getBudget() {
        return budget;
    }
}
