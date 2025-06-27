package io.github.janvinas.trainmanager.signs;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import io.github.janvinas.trainmanager.TrainManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SignActionLeaveStation extends SignAction {

    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("leavestation");
    }

    @Override
    public void execute(SignActionEvent info) {
        String station = info.getLine(2);
        if(station == null) return;

        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            if (!info.isPowered()) return;
            String trainName = info.getGroup().getProperties().getTrainName();
            JavaPlugin.getPlugin(TrainManager.class).trainTracker.leaveStation(trainName, station);
        }
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (!event.isType("leavestation")) {
            return false;
        }

        return SignBuildOptions.create()
                .setName("leavestation")
                .setDescription("Triggered when a train leaves the station. Updates the tracked accordingly.")
                .handle(event.getPlayer());
    }
}
