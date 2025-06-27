package io.github.janvinas.trainmanager.displays;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import io.github.janvinas.trainmanager.TrainManager;
import io.github.janvinas.trainmanager.tracker.Departure;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TreeMap;

public class DepartureBoard1 extends MapDisplay {
    int tickCount = -1;
    final int updateInterval = 100;

    String station;

    @Override
    public void onAttached() {
        setUpdateWithoutViewers(false);
        station = properties.get("station", String.class, "");

        getLayer(0).clear();
        getLayer().fillRectangle(0, 0, 256, 10, MapColorPalette.getColor(72, 129, 183));
        getLayer().fillRectangle(0, 10, 256, 1, MapColorPalette.getColor(0, 0, 0));
        getLayer().draw(MapFont.MINECRAFT, 1, 1, MapColorPalette.getColor(255, 255, 255), "Departures:");
        getLayer().setAlignment(MapFont.Alignment.RIGHT);
        getLayer().draw(MapFont.MINECRAFT, 254, 1, MapColorPalette.getColor(255, 255, 255), station);
        //draw background strips
        getLayer().fillRectangle(0, 11, 256, 10, MapColorPalette.getColor(255, 255, 255));
        getLayer().fillRectangle(0, 21, 256, 10, MapColorPalette.getColor(200, 200, 200));
        getLayer().fillRectangle(0, 31, 256, 10, MapColorPalette.getColor(255, 255, 255));
        getLayer().fillRectangle(0, 41, 256, 10, MapColorPalette.getColor(200, 200, 200));
        getLayer().fillRectangle(0, 51, 256, 10, MapColorPalette.getColor(255, 255, 255));
        getLayer().fillRectangle(0, 61, 256, 10, MapColorPalette.getColor(200, 200, 200));
        getLayer().fillRectangle(0, 71, 256, 10, MapColorPalette.getColor(255, 255, 255));
        getLayer().fillRectangle(0, 81, 256, 10, MapColorPalette.getColor(200, 200, 200));
        getLayer().fillRectangle(0, 91, 256, 10, MapColorPalette.getColor(255, 255, 255));
        getLayer().fillRectangle(0, 101, 256, 10, MapColorPalette.getColor(200, 200, 200));

        //draw column titles:
        getLayer().setAlignment(MapFont.Alignment.LEFT);
        getLayer().draw(MapFont.MINECRAFT, 1, 12, MapColorPalette.getColor(0, 0, 255), "Time");
        getLayer().draw(MapFont.MINECRAFT, 80, 12, MapColorPalette.getColor(0, 0, 255), "Line");
        getLayer().draw(MapFont.MINECRAFT, 107, 12, MapColorPalette.getColor(0, 0, 255), "Destination");
        getLayer().draw(MapFont.MINECRAFT, 170, 12, MapColorPalette.getColor(0, 0, 255), "Pl.");
        getLayer().draw(MapFont.MINECRAFT, 190, 12, MapColorPalette.getColor(0, 0, 255), "Information");
        //draw last line, where time and date will be shown
        getLayer().fillRectangle(0, 111, 256, 18, MapColorPalette.getColor(0, 0, 0));
        getLayer().fillRectangle(0, 112, 256, 10, MapColorPalette.getColor(72, 129, 183));
    }

    @Override
    public void onTick() {
        tickCount++;
        tickCount %= updateInterval;

        LocalDateTime now = LocalDateTime.now();
        getLayer(3).clear();
        getLayer(3).setAlignment(MapFont.Alignment.LEFT);
        getLayer(3).draw(MapFont.MINECRAFT, 1, 113, MapColorPalette.getColor(0, 0, 0), now.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " UTC");
        getLayer(3).setAlignment(MapFont.Alignment.RIGHT);
        getLayer(3).draw(MapFont.MINECRAFT, 254, 113, MapColorPalette.getColor(0, 0, 0), now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if(tickCount != 0) return;

        getLayer(1).clear();
        getLayer(1).setAlignment(MapFont.Alignment.LEFT);

        TreeMap<LocalDateTime, Departure> list = ((TrainManager) getPlugin()).trainTracker.getNextDepartures(station, 8);
        ArrayList<LocalDateTime> times = new ArrayList<>(list.keySet());
        ArrayList<Departure> departures = new ArrayList<>(list.values());
        for(int i = 0; i <= 8; i++){
            getLayer(1).draw(MapFont.MINECRAFT, 1, 22 + i*10,
                    MapColorPalette.getColor(0, 0, 0),
                    times.get(i).format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            getLayer(1).draw(MapFont.MINECRAFT, 80, 22 + i*10,
                    MapColorPalette.getColor(0, 0, 0),
                    departures.get(i).line);
            getLayer(1).draw(MapFont.MINECRAFT, 107, 22 + i*10,
                    MapColorPalette.getColor(0, 0, 0),
                    departures.get(i).destination);
            getLayer(1).draw(MapFont.MINECRAFT, 170, 22 + i*10,
                    MapColorPalette.getColor(0, 0, 0),
                    "");    //TODO platform not implemented yet

            if(departures.get(i).delay != null && departures.get(i).delay.toSeconds() > 10){
                getLayer(1).draw(MapFont.MINECRAFT, 190, 22 + i*10,
                        MapColorPalette.getColor(170, 0, 0),
                        "Delayed " + departures.get(i).delay.toSeconds() + "s");
            }else if(departures.get(i).delay != null){
                getLayer(1).draw(MapFont.MINECRAFT, 190, 22 + i*10,
                        MapColorPalette.getColor(0, 170, 0),
                        "On time");
            }


        }
    }
}
