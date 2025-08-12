package io.github.janvinas.trainmanager;

import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapDisplayProperties;
import com.bergerkiller.bukkit.tc.events.GroupRemoveEvent;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import io.github.janvinas.trainmanager.displays.DepartureBoard1;
import io.github.janvinas.trainmanager.displays.DepartureBoard2;
import io.github.janvinas.trainmanager.signs.SignActionLeaveStation;
import io.github.janvinas.trainmanager.tracker.TrackedTrain;
import io.github.janvinas.trainmanager.tracker.TrainTracker;
import io.github.janvinas.trainmanager.variableDisplays.VariableDisplayManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.util.TreeMap;
import java.util.logging.Level;

public final class TrainManager extends JavaPlugin {

    public class EventListener implements Listener {
        @EventHandler
        public void onGroupRemove(GroupRemoveEvent event){
            trainTracker.removeTrain(event.getGroup().getProperties().getTrainName());
        }
    }

    public TrainTracker trainTracker = new TrainTracker(this);
    TrainSpawner trainSpawner = new TrainSpawner(this, trainTracker);
    VariableDisplayManager variableDisplayManager;

    private void loadTrains(){
        File savedTrains = new File(getDataFolder(), "savedtrains.ser");
        if(!savedTrains.exists()) return;
        if(!savedTrains.isDirectory()) return;

        try {
            ObjectInputStream s = new ObjectInputStream(new FileInputStream(savedTrains));
            trainTracker.trackedTrains = (TreeMap<String, TrackedTrain>) s.readObject();
        }catch(Exception e){
            getLogger().log(Level.WARNING, "Error reading train file. Ignore this if it's the first time using the plugin.");
            return;
        }
        getLogger().log(Level.INFO, "Loaded tracked trains");
    }

    private void saveTrains(){
        File savedTrains = new File(getDataFolder(), "savedtrains.ser");
        try{
            savedTrains.createNewFile();
            ObjectOutputStream s = new ObjectOutputStream(new FileOutputStream(savedTrains));
            s.writeObject(trainTracker.trackedTrains);
            s.close();
        }catch(IOException e){
            getLogger().log(Level.WARNING, "Error saving trains");
            return;
        }
        getLogger().log(Level.INFO, "Saved tracked trains");

    }

    @Override
    public void onEnable() {
        // force loading of resources
        Resources.getInstance();

        loadTrains();
        trainTracker.start();
        trainSpawner.start();

        variableDisplayManager = new VariableDisplayManager(this);
        variableDisplayManager.start();

        SignAction.register(new SignActionLeaveStation());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        saveTrains();
        variableDisplayManager.stop();
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if(command.getName().equals("trainmanager")) {
            if (args.length == 1 && (args[0].equals("info") || args[0].equals("i"))) {
                trainTracker.sendTrainInformation(CartProperties.getEditing((Player) sender), (Player) sender);
            }else if(args.length == 2 && args[0].equals("departures")){
                trainTracker.sendStationInformation(args[1].replace('_', ' '), (Player) sender);
            }else if(args.length == 3 && args[0].equals("getdisplay")) {
                ItemStack display = null;
                if (args[1].equals("1")) {
                    display = MapDisplay.createMapItem(DepartureBoard1.class);
                } else if (args[1].equals("2")) {
                    display = MapDisplay.createMapItem(DepartureBoard2.class);
                }
                if (display == null) return true;

                MapDisplayProperties.of(display).set("station", args[2].replace("_", " "));
                MapDisplay.getAllDisplays(display).forEach(MapDisplay::restartDisplay);
                ((Player) sender).getInventory().addItem(display);
            } else if(args.length == 5 && args[0].equals("vdisplay")) {
                String stationName = args[1].replace("_", " ");
                String prefix = args[2];
                String platform = args[3];
                int period = Integer.parseInt(args[4]);
                variableDisplayManager.addSign(platform, prefix, stationName, period);
                sender.sendMessage("Variables created");
            } else if (args.length == 1 && args[0].equals("vdisplays")) {
                variableDisplayManager.getDisplays().forEach((d) -> {
                    sender.sendMessage("Prefix=" + d.getVariablePrefix() +
                                    " Station=" + d.getStationName() +
                                    " Period=" + d.getPeriod() +
                                    " Platform=" + d.getPlatform());
                });
            }
        }

        return true;
    }
}
