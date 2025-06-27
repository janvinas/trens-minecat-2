package io.github.janvinas.trainmanager;

import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.spawnable.SpawnableGroup;
import io.github.janvinas.trainmanager.tracker.Service;
import io.github.janvinas.trainmanager.tracker.TrackingProperties;
import io.github.janvinas.trainmanager.tracker.TrainTracker;
import it.sauronsoftware.cron4j.Scheduler;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class TrainSpawner {

    public static class QueuedSpawn {
        String world;
        int x;
        int y;
        int z;
        String heading;
        String train;
        String trainName;
        TrackingProperties trackingProperties;
    }

    Scheduler scheduler;
    TrainManager plugin;
    TrainTracker tracker;
    ArrayBlockingQueue<QueuedSpawn> pendingSpawns = new ArrayBlockingQueue<>(128);

    public TrainSpawner(TrainManager plugin, TrainTracker tracker){
        this.plugin = plugin;
        this.tracker = tracker;
    }

    public void start(){
        scheduler = new Scheduler();
        loadTrains();
        scheduler.start();
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            while(pendingSpawns.size() > 0){
                QueuedSpawn s;
                try {
                    s = pendingSpawns.take();
                    spawnTrain(s.world, s.x, s.y, s.z, s.heading, s.train, s.trainName, s.trackingProperties);
                    plugin.getLogger().log(Level.INFO, "Spawned train " + s.trainName);
                } catch (InterruptedException ignored) { }

            }
        }, 0, 1);
    }

    public void spawnTrain(String world, int x, int y, int z, String heading, String train, String trainName, @Nullable TrackingProperties trackingProperties){
        SpawnableGroup spawnableGroup = SpawnableGroup.parse(JavaPlugin.getPlugin(TrainCarts.class), train);
        Vector headingVector = switch (heading){
            case "north" -> new Vector(0, 0, -1);
            case "south" -> new Vector(0, 0, 1);
            case "east" -> new Vector(1, 0, 0);
            case "west" -> new Vector(-1, 0, 0);
            default -> new Vector(0, 0, 0);
        };
        SpawnableGroup.SpawnLocationList spawnLocationList = spawnableGroup.findSpawnLocations(
                new Location(plugin.getServer().getWorld(world), x, y, z), headingVector, SpawnableGroup.SpawnMode.DEFAULT
        );

        spawnLocationList.loadChunks();
        MinecartGroup minecartGroup = spawnableGroup.spawn(spawnLocationList);
        minecartGroup.getProperties().setTrainName(trainName);
        minecartGroup.getProperties().setKeepChunksLoaded(true);    //all automatic trains should load chunks

        if(trackingProperties != null){
            tracker.trackTrain(trainName, trackingProperties);
        }
    }

    public void loadTrains(){
        File serviceFileList = new File(plugin.getDataFolder(), "services");
        if(!serviceFileList.exists() || !serviceFileList.isDirectory()) return;

        for (File file : Objects.requireNonNull(serviceFileList.listFiles())){
            if(!file.isDirectory()) loadServiceFile(file);
        }

    }

    public void loadServiceFile(File file){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error loading service file " + file.getName());
            return;
        }

        Element root = document.getDocumentElement();
        if(!root.getTagName().equals("service")) {
            plugin.getLogger().log(Level.WARNING, "Error loading service file " + file.getName());
            return;
        }

        Node destinationNode = root.getElementsByTagName("destination").item(0);
        String destination = null;
        if(destinationNode != null){
            destination = destinationNode.getTextContent();
        }

        Node lineNode = root.getElementsByTagName("line").item(0);
        String line = null;
        if(lineNode != null){
            line = lineNode.getTextContent();
        }

        String time = root.getElementsByTagName("time").item(0).getTextContent();
        String world = root.getElementsByTagName("world").item(0).getTextContent();
        String heading = root.getElementsByTagName("heading").item(0).getTextContent();
        String train = root.getElementsByTagName("train").item(0).getTextContent();
        int x = Integer.parseInt(root.getElementsByTagName("x").item(0).getTextContent());
        int y = Integer.parseInt(root.getElementsByTagName("y").item(0).getTextContent());
        int z = Integer.parseInt(root.getElementsByTagName("z").item(0).getTextContent());

        TrackingProperties props = new TrackingProperties(line, destination);
        NodeList stations = root.getElementsByTagName("stationList").item(0).getChildNodes();
        for(int i = 0; i < stations.getLength(); i++){
            if(!stations.item(i).getNodeName().equals("station")) continue;
            String name = stations.item(i).getTextContent();
            String departuretime = ((Element) stations.item(i)).getAttribute("departuretime");
            String newDestination = ((Element) stations.item(i)).getAttribute("newdest");
            if(newDestination.equals("")) newDestination = null;

            Duration departuretimeDuration = Duration.ofSeconds(Integer.parseInt(departuretime));
            props.stationList.put(departuretimeDuration, new TrackingProperties.Station(name, newDestination));
        }

        plugin.trainTracker.registerService(new Service(line, destination, time, props.stationList));

        scheduler.schedule(time, () -> {
            props.spawnTime = LocalDateTime.now().withNano(0);
            QueuedSpawn queuedSpawn = new QueuedSpawn();
            queuedSpawn.world = world;
            queuedSpawn.x = x;
            queuedSpawn.y = y;
            queuedSpawn.z = z;
            queuedSpawn.heading = heading;
            queuedSpawn.train = train;
            queuedSpawn.trainName = props.line + "_" + DateTimeFormatter.ofPattern("HHmm").format(props.spawnTime);
            queuedSpawn.trackingProperties = props;
            try {
                pendingSpawns.put(queuedSpawn);
            } catch (InterruptedException ignore) {}
        });
    }

}
