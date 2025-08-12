package io.github.janvinas.trainmanager.tracker;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeMap;

public class TrackingProperties {
    public static class Station implements Serializable {
        String name;
        String platform;
        String newDestination;

        public Station(String name, String destination, String platform){
            this.name = name;
            this.newDestination = destination;
            this.platform = platform;
        }
    }
    public String line;
    public String destination;
    public LocalDateTime spawnTime;
    public TreeMap<Duration, Station> stationList = new TreeMap<>();

    public TrackingProperties(String line, String destination){
        this.line = line;
        this.destination = destination;
    }
}