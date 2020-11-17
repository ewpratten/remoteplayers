package ca.retrylife.mc.remoteplayers.dynmap;

import java.io.IOException;
import java.net.URL;

import ca.retrylife.mc.remoteplayers.utils.HTTP;

/**
 * Represents a connection to a dynmap server
 */
public class DynmapConnection {

    // URL
    private URL queryURL;
    private String defaultWorldName;

    /**
     * Create a new dynmap connection
     * 
     * @param baseURL Base URL of the dynmap server
     * @throws IOException
     */
    public DynmapConnection(URL baseURL) throws IOException {

        // Get the default world name
        defaultWorldName = ((DynmapConfiguration) HTTP.makeJSONHTTPRequest(
                new URL(baseURL.toString() + "/up/configuration"), DynmapConfiguration.class)).defaultworld;

        // Build the url
        queryURL = new URL(baseURL.toString() + "/up/world/" + defaultWorldName + "/");

    }

    /**
     * Ask the server for a list of all player positions
     * 
     * @return Player positions
     * @throws IOException
     */
    public PlayerPosition[] getAllPlayerPositions() throws IOException {

        // Make request for all players
        DynmapUpdate update = HTTP.makeJSONHTTPRequest(queryURL, DynmapUpdate.class);

        // Build a list of positions
        PlayerPosition[] positions = new PlayerPosition[update.players.length];

        for (int i = 0; i < update.players.length; i++) {
            // Player must be in main world
            if (!update.players[i].world.equals(defaultWorldName)) {
                continue;
            }

            positions[i] = new PlayerPosition(update.players[i].name, update.players[i].x, update.players[i].y,
                    update.players[i].z);
        }

        return positions;
    }

}