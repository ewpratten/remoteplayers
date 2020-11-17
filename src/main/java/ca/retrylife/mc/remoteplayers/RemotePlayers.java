package ca.retrylife.mc.remoteplayers;

import net.fabricmc.api.ModInitializer;

import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import ca.retrylife.mc.remoteplayers.dynmap.DynmapConnection;

/**
 * Mod entrypoint. Sets everything up
 */
public class RemotePlayers implements ModInitializer {
    private static Logger logger = LogManager.getLogger(RemotePlayers.class);

    // Update task
    private static UpdateTask updateTask = new UpdateTask();
    private static Timer dynmapUpdateThread = null;

    // Connection to Dynmap
    private static DynmapConnection connection = null;

    // Online status
    public static boolean isOnline = false;
    public static boolean isDisabled = false;

    @Override
    public void onInitialize() {
        logger.info("RemotePlayers starting...");
        // logger.info(
        // String.format("RemotePlayers hooked in to Minimap version: %s",
        // XaeroMinimap.instance.getVersionID()));

        // Begin a thread for updating the map
        dynmapUpdateThread = new Timer(true);
        dynmapUpdateThread.scheduleAtFixedRate(updateTask, 0, 2000);

        logger.info("RemotePlayers started");
    }

    /**
     * Set how often to check for player position updates
     * 
     * @param seconds Time in seconds
     */
    public static void setUpdateDelay(double seconds) {
        updateTask.cancel();
        dynmapUpdateThread.scheduleAtFixedRate(updateTask, 0, (long) (1000.0 * seconds));
        logger.info(String.format("Dynmap update delay has been set to %.2f seconds", seconds));
    }

    /**
     * Sets the current dynmap connection
     * 
     * @param connection Connection
     */
    public static void setConnection(DynmapConnection connection) {
        RemotePlayers.connection = connection;
    }

    /**
     * Gets the current dynmap connection
     * 
     * @return Connection
     */
    public static @Nullable DynmapConnection getConnection() {
        return RemotePlayers.connection;
    }

}