package ca.retrylife.mc.remoteplayers.dynmap;

import com.mojang.authlib.GameProfile;

/**
 * A player's auth profile and position
 */
public class PlayerPosition {

    public final GameProfile player;
    public final int x;
    public final int y;
    public final int z;

    public PlayerPosition(String username, int x, int y, int z) {
        this.player = new GameProfile(null, username);
        this.x = x;
        this.y = y;
        this.z = z;
    }

}