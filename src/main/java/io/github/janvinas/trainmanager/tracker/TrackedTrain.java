package io.github.janvinas.trainmanager.tracker;

import org.bukkit.event.world.GenericGameEvent;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrackedTrain implements Serializable {

    public enum TrainStatus{
        running,
        stationed
    }
    final String line;
    String destination;
    final LocalDateTime spawnTime;

    private TrainStatus status = TrainStatus.stationed;
    Duration delay;

    TreeMap<LocalDateTime, TrackingProperties.Station> nextStations = new TreeMap<>();  //scheduled times

    TrackedTrain(TrackingProperties props){
        this.line = props.line;
        this.destination = props.destination;
        this.spawnTime = props.spawnTime;

        delay = Duration.ZERO;

        props.stationList.forEach((Duration d, TrackingProperties.Station station) -> {
            nextStations.put(props.spawnTime.plus(d), station);
        });


    }

    public void setStatus(TrainStatus status){
        this.status = status;
    }
    public TrainStatus getStatus(){
        return this.status;
    }

    //removes the station where we just left from the map and all previous ones.
    public boolean leaveStation(String station){
        AtomicBoolean found = new AtomicBoolean(false);
        nextStations.forEach((LocalDateTime t, TrackingProperties.Station s) -> {
            if(s.name.equals(station)) found.set(true);
        });
        if(!found.get()) return false;

        while(nextStations.size() > 0){
            Map.Entry<LocalDateTime, TrackingProperties.Station> entry = nextStations.pollFirstEntry();
            if(entry.getValue().newDestination != null) this.destination = entry.getValue().newDestination;
            if(station.equals(entry.getValue().name)) {
                this.delay = Duration.between(entry.getKey(), LocalDateTime.now());
                return true;
            }
        }

        return true; // intellij would complain otherwise :(
    }

}
