package io.github.janvinas.trainmanager;

import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Resources {
    private static Resources instance;

    public Font interFont;
    public Font minecraftFont;

    private Resources() {
        JavaPlugin plugin = JavaPlugin.getPlugin(TrainManager.class);
        
        // load resources:

        try (InputStream stream = this.getClass().getResourceAsStream("/Inter.ttf")) {
            assert stream != null;
            interFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            plugin.getLogger().severe("Failed to load Inter font");
        }

        try (InputStream stream = this.getClass().getResourceAsStream("/Minecraft.otf")) {
            assert stream != null;
            minecraftFont = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            plugin.getLogger().severe("Failed to load Minecraft font");
        }
    }

    public static Resources getInstance() {
        if (instance == null) {
            instance = new Resources();
        }
        return instance;
    }
}
