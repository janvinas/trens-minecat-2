package io.github.janvinas.trainmanager.variableDisplays;

import io.github.janvinas.trainmanager.TrainManager;

import java.util.ArrayList;

public class VariableDisplayManager {
    private final TrainManager plugin;
    private final VariableDisplayStore variableDisplayStore;

    public VariableDisplayManager(TrainManager plugin) {
        this.plugin = plugin;
        variableDisplayStore = new VariableDisplayStore(plugin);
        variableDisplayStore.loadDisplays();
    }

    public void start() {
        variableDisplayStore.variableDisplays.forEach((display) -> display.start(plugin));
    }

    public void stop() {
        variableDisplayStore.variableDisplays.forEach((display) -> display.stop(plugin));
        variableDisplayStore.saveDisplays();
    }

    public void addSign(String platform, String variablePrefix, String stationName, int period) {
        var display = new VariableDisplay(platform, variablePrefix, stationName, period);
        variableDisplayStore.variableDisplays.add(display);
        display.start(plugin);

    }

    public ArrayList<VariableDisplay> getDisplays() {
        return variableDisplayStore.variableDisplays;
    }
}
