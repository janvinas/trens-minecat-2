package io.github.janvinas.trainmanager.tracker;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.properties.TrainPropertiesStore;
import it.sauronsoftware.cron4j.Predictor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TrainTracker {

    JavaPlugin plugin;
    ArrayList<Service> services = new ArrayList<>();
    public TreeMap<String, TrackedTrain> trackedTrains = new TreeMap<>();

    public TrainTracker(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public void start(){
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            HashSet<String> keySet = new HashSet<>(trackedTrains.keySet());
            keySet.forEach((String s) -> {
                if(!TrainPropertiesStore.exists(s)){
                    trackedTrains.remove(s);
                }
            });
        }, 0, 600);
    }

    public void registerService(Service s) {
        this.services.add(s);
    }

    public void clearServices() {
        this.services.clear();
    }

    public void trackTrain(String trainName, TrackingProperties props){
        TrackedTrain trackedTrain = new TrackedTrain(props);
        trackedTrains.put(trainName, trackedTrain);
    }

    public void removeTrain(String trainName){
        trackedTrains.remove(trainName);
    }

    public boolean leaveStation(String trainName, String station){
        if(!trackedTrains.containsKey(trainName)) return false;
        return trackedTrains.get(trainName).leaveStation(station);
    }

    public TreeMap<LocalDateTime, Departure> getNextDepartures(String station, int min){
        TreeMap<LocalDateTime, Departure> nextDepartures = new TreeMap<>();

        this.services.forEach((Service service) -> {
            final String[] destination = {service.destination};

            service.stationList.forEach((Duration d, TrackingProperties.Station s) -> {
                if(s.newDestination != null) destination[0] = s.newDestination;
                if(station.equals(s.name)) {
                    Predictor p = new Predictor(service.cronExpression);
                    for(int i = 0; i <= min; i++){
                        LocalDateTime spawn = p.nextMatchingDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        LocalDateTime departure = spawn.plus(d);
                        nextDepartures.put(departure, new Departure(service.line, destination[0], s.platform, spawn, null));
                    }
                }
            });
        });

        // very computationally expensive!! :(
        trackedTrains.forEach((String trainName, TrackedTrain trackedTrain) -> {
            AtomicBoolean found = new AtomicBoolean(false);
            nextDepartures.forEach((LocalDateTime departureTime, Departure departure) -> {
                if(departure.name.equals(trainName)){
                    Duration delay = trackedTrain.delay;
                    departure.delay = delay.isNegative() ? Duration.ZERO : delay;
                    found.set(true);
                }
            });
            if(!found.get()){
                AtomicReference<String> destination = new AtomicReference<>(trackedTrain.destination);
                trackedTrain.nextStations.forEach((LocalDateTime d, TrackingProperties.Station s) -> {
                    if(s.newDestination != null) destination.set(s.newDestination);
                    if(s.name.equals(station)){
                        nextDepartures.put(d, new Departure(trackedTrain.line, destination.get(), s.platform, trackedTrain.spawnTime, trackedTrain.delay));
                    }
                });
            }

        });

        return nextDepartures;
    }

    public TreeMap<LocalDateTime, Departure> getNextDepartures(String station, String platform, int min) {
        var departures = getNextDepartures(station, min);
        TreeMap<LocalDateTime, Departure> filteredDepartures = new TreeMap<>();
        departures.forEach((LocalDateTime departureTime, Departure departure) -> {
            if (departure.platform.equals(platform)) {
                filteredDepartures.put(departureTime, departure);
            }
        });
        return filteredDepartures;
    }

    public void sendStationInformation(String station, Player player){
        player.sendMessage(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Next departures for " + station);

        getNextDepartures(station, 5).forEach((LocalDateTime time, Departure departure) -> {
            String timeInfo;

            if(departure.delay == null) {
                timeInfo = ChatColor.GRAY + DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
            }else if(departure.delay.toSeconds() < 10){
                timeInfo = ChatColor.GREEN + DateTimeFormatter.ofPattern("HH:mm:ss").format(time);
            }else{
                timeInfo = ChatColor.RED + "" + ChatColor.STRIKETHROUGH + DateTimeFormatter.ofPattern("HH:mm:ss").format(time) +
                        ChatColor.RESET + " " + ChatColor.GRAY + DateTimeFormatter.ofPattern("HH:mm:ss").format(time.plus(departure.delay));
            }

            player.sendMessage(timeInfo + ChatColor.RESET + " -- " + ChatColor.BOLD + departure.line + " " + ChatColor.RESET + departure.destination);
        });
    }

    public void sendTrainInformation(CartProperties props, Player player){
        if(props == null){
            player.sendMessage(ChatColor.RED + "You are not editing any train!");
            return;
        }
        String trainName = props.getTrainProperties().getTrainName();
        TrackedTrain trackedTrain = trackedTrains.get(trainName);
        if(trackedTrain == null){
            player.sendMessage(ChatColor.RED + "This train is not being tracked");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "" + ChatColor.UNDERLINE + "Tracking train " + trainName);
        player.sendMessage(ChatColor.AQUA + "Delay:  " + trackedTrain.delay.toSeconds() + " seconds.");
        trackedTrain.nextStations.forEach((LocalDateTime t, TrackingProperties.Station s) -> {
            player.sendMessage(ChatColor.AQUA + t.toString() + " -- " + s.name);
        });

    }

}