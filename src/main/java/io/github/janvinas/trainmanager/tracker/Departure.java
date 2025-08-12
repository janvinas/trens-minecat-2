package io.github.janvinas.trainmanager.tracker;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Departure {
    public String line;
    public String destination;
    public String name;
    public String platform;
    public Duration delay;

    public Departure(String line, String destination, String platform, LocalDateTime spawn, Duration delay){
        this.line = line;
        this.destination = destination;
        this.delay = delay;
        this.platform = platform;
        this.name = line + "_" + DateTimeFormatter.ofPattern("HHmm").format(spawn);
    }
}
