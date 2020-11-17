package ca.retrylife.mc.remoteplayers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.TimerTask;

import ca.retrylife.mc.remoteplayers.dynmap.DynmapConnection;
import ca.retrylife.mc.remoteplayers.dynmap.PlayerPosition;
import ca.retrylife.mc.remoteplayers.utils.PlayerWaypoint;
import net.minecraft.client.MinecraftClient;
import xaero.common.XaeroMinimapSession;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Threaded task that is run once every few seconds to fetch data from dynmap
 * and update the local maps
 */
public class UpdateTask extends TimerTask {
    private Logger logger = LogManager.getLogger(getClass());

    // Name of the player waypoint set
    private static final String DEFAULT_PLAYER_SET_NAME = "RemotePlayers_Temp";

    // Access to minecraft
    private MinecraftClient mc;

    public UpdateTask() {
        this.mc = MinecraftClient.getInstance();
    }

    @Override
    public void run() {

        // Skip if not in game
        if (mc.isInSingleplayer() || mc.getNetworkHandler() == null
                || !mc.getNetworkHandler().getConnection().isOpen()) {
            RemotePlayers.isOnline = false;
            RemotePlayers.isDisabled = false;
            return;
        }
        RemotePlayers.isOnline = true;

        // Get the IP of this server
        String serverIP = mc.getCurrentServerEntry().address;

        // Handle setting up a new connection to dynmap
        if (RemotePlayers.getConnection() == null && Database.getInstance().serverHasDynmapLinked(serverIP)
                && !RemotePlayers.isDisabled) {
            try {
                RemotePlayers.setConnection(
                        new DynmapConnection(new URL(Database.getInstance().getConfiguredDynmapForServer(serverIP))));
            } catch (IOException e) {
                e.printStackTrace();
                RemotePlayers.isDisabled = true;
            }
        }
        if (RemotePlayers.getConnection() == null) {
            return;
        }

        // Access the current waypoint world
        WaypointWorld currentWorld = XaeroMinimapSession.getCurrentSession().getWaypointsManager().getCurrentWorld();

        // Skip if the world is null
        if (currentWorld == null) {
            logger.info("Player left world, disconnecting from dynmap");
            RemotePlayers.setConnection(null);
            return;
        }

        // Get a list of all player's positions
        PlayerPosition[] positions;
        try {
            positions = RemotePlayers.getConnection().getAllPlayerPositions();
        } catch (IOException e) {
            logger.warn("Failed to make Dynmap request");
            e.printStackTrace();
            RemotePlayers.setConnection(null);
            RemotePlayers.isDisabled = true;
            return;
        }

        // Get (and possibly create) the needed waypoint set
        if (!Database.getInstance().getIntegrationEnabled()) {
            if (!currentWorld.getSets().containsKey(DEFAULT_PLAYER_SET_NAME)) {
                currentWorld.addSet(DEFAULT_PLAYER_SET_NAME);
            }
        }

        // Fetch the correct list of waypoints
        ArrayList<Waypoint> waypointList = null;
        if (Database.getInstance().getIntegrationEnabled()) {
            waypointList = currentWorld.getCurrentSet().getList();
        } else {
            waypointList = currentWorld.getSets().get(DEFAULT_PLAYER_SET_NAME).getList();
        }

        try {
            synchronized (waypointList) {

                // Clear all waypoints from the temp set
                for (Waypoint waypoint : waypointList) {
                    if (waypoint instanceof PlayerWaypoint && waypoint.isTemporary()) {
                        waypointList.remove(waypoint);
                    }
                }

                // Add each player to the map
                for (PlayerPosition playerPosition : positions) {
                    // If this player is self, skip
                    // if (mc.player !=null && mc.player.getGameProfile() != null &&
                    // mc.player.getGameProfile().getName() != null &&
                    // playerPosition.player.getName() == mc.player.getGameProfile().getName()) {
                    // continue;
                    // }

                    // Add waypoint for the player
                    try {
                        waypointList.add(new PlayerWaypoint(playerPosition));
                    } catch (NullPointerException e) {
                        continue;
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            return;
        }

    }

}