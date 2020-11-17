package ca.retrylife.mc.remoteplayers.utils;

import ca.retrylife.mc.remoteplayers.dynmap.PlayerPosition;
import xaero.common.minimap.waypoints.Waypoint;

/**
 * A wrapper to improve creating temp waypoints for players
 */
public class PlayerWaypoint extends Waypoint {

    public PlayerWaypoint(PlayerPosition player) {
        this(player.x, player.y, player.z, player.player.getName());
    }

    public PlayerWaypoint(int x, int y, int z, String name) {
        super(x, y, z, name, "P", 0, 0, true);
    }

}