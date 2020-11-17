package ca.retrylife.mc.remoteplayers.dynmap;

/**
 * JSON object from dynmap API. Send in update requests
 */
public class DynmapUpdate {

    public static class Player {
        public String name;
        public String world;
        public int x;
        public int y;
        public int z;
    }

    public Player[] players;

}