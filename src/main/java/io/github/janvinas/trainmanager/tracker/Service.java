package io.github.janvinas.trainmanager.tracker;

import java.time.Duration;
import java.util.TreeMap;

public class Service {
    String line;
    String destination;
    String cronExpression;
    public TreeMap<Duration, TrackingProperties.Station> stationList;

    public Service(String line, String destination, String cronExpression, TreeMap<Duration, TrackingProperties.Station> stationList){
        this.line = line;
        this.destination = destination;
        this.cronExpression = cronExpression;
        this.stationList = stationList;
    }
}
