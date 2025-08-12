package io.github.janvinas.trainmanager.displays;

import com.bergerkiller.bukkit.common.map.MapColorPalette;
import com.bergerkiller.bukkit.common.map.MapDisplay;
import com.bergerkiller.bukkit.common.map.MapFont;
import io.github.janvinas.trainmanager.Resources;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DepartureBoard2 extends MapDisplay {
    int tickCount = -1;
    final int UPDATE_INTERVAL = 100; // 5 seconds
    String station;

    final byte COLOR_BACKGROUND = MapColorPalette.getColor(50, 50, 50);
    final byte COLOR_ACCENT = MapColorPalette.getColor(255, 255, 0);

    @Override
    public void onAttached() {
        setUpdateWithoutViewers(false);
        station = properties.get("station", String.class, "");

        getLayer(0).clear();
        getLayer(0).fillRectangle(0, 0, 256, 128, COLOR_BACKGROUND);

        super.onAttached();
    }

    @Override
    public void onTick() {
        tickCount++;
        tickCount %= UPDATE_INTERVAL;
        if (tickCount != 0) return;

        getLayer(3).clear();

        BufferedImage layer3 = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = layer3.createGraphics();
        Font minecraftFont = Resources.getInstance().minecraftFont.deriveFont(10F);

        super.onTick();
    }
}
