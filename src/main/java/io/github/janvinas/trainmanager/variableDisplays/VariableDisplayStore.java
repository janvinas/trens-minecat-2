package io.github.janvinas.trainmanager.variableDisplays;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;

public class VariableDisplayStore {
    JavaPlugin plugin;
    public ArrayList<VariableDisplay> variableDisplays;

    public VariableDisplayStore(JavaPlugin plugin) {
        this.plugin = plugin;
        variableDisplays = new ArrayList<>();
    }

    public void loadDisplays() {
        try {
            File file =  new File(plugin.getDataFolder(), "variableDisplays");
            if(!file.exists()) return;

            var fileInputStream = new FileInputStream(file);
            var objectInputStream = new ObjectInputStream(fileInputStream);

            variableDisplays = (ArrayList<VariableDisplay>) objectInputStream.readObject();
        } catch (IOException e) {
            plugin.getLogger().warning("Could not load variable displays");
        } catch (ClassNotFoundException | ClassCastException e) {
            plugin.getLogger().warning("Could not parse variable displays file");
        }
    }

    public void saveDisplays() {
        try {
            File file =  new File(plugin.getDataFolder(), "variableDisplays");
            file.createNewFile();
            var fileOutputStream = new FileOutputStream(file, false);
            var objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(variableDisplays);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save variable displays");
        }
    }
}
