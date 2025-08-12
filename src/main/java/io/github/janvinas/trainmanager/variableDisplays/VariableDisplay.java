package io.github.janvinas.trainmanager.variableDisplays;

import com.bergerkiller.bukkit.sl.API.TickMode;
import com.bergerkiller.bukkit.sl.API.Variables;
import io.github.janvinas.trainmanager.TrainManager;
import io.github.janvinas.trainmanager.tracker.Departure;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.TreeMap;

public class VariableDisplay implements Serializable {
    private String stationName;
    private String variablePrefix;
    private String platform;
    private int period;

    transient private int task;
    transient private TrainManager plugin;

    public VariableDisplay(String platform, String variablePrefix, String stationName, int period) {
        this.platform = platform;
        this.variablePrefix = variablePrefix;
        this.stationName = stationName;
        this.period = period;
    }

    public String getVariablePrefix() {
        return variablePrefix;
    }

    public void setVariablePrefix(String variablePrefix) {
        this.variablePrefix = variablePrefix;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public void start(TrainManager plugin) {
        this.task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            TreeMap<LocalDateTime, Departure> list = plugin.trainTracker.getNextDepartures(stationName, platform, 1);
            if (list.isEmpty()) return;
            var time = list.firstKey();
            var departure = list.firstEntry().getValue();

            var timevar = Variables.get(variablePrefix + "_t");
            timevar.set(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            if (departure.delay != null && LocalDateTime.now().isAfter(time.plus(departure.delay).minusSeconds(15))) {
                timevar.getTicker().setMode(TickMode.BLINK);
                timevar.getTicker().setInterval(10);
            } else {
                timevar.getTicker().setMode(TickMode.NONE);
            }

            Variables.get(variablePrefix + "_n").set(departure.name);
            Variables.get(variablePrefix + "_l").set(departure.line);
            Variables.get(variablePrefix + "_d").set(departure.destination);
            Variables.get(variablePrefix + "_ld").set(departure.line + " " + departure.destination);

            if (departure.delay == null) {
                Variables.get(variablePrefix + "_i").set("");
            } else if (departure.delay.toSeconds() < 10) {
                Variables.get(variablePrefix + "_i").set("On time");
            } else {
                Variables.get(variablePrefix + "_i").set("Delayed " + departure.delay.toSeconds() + "s");
            }

        }, 0, this.period);
    }

    public void stop(TrainManager plugin) {
        plugin.getServer().getScheduler().cancelTask(task);
    }
}
