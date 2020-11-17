package ca.retrylife.mc.remoteplayers.commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import ca.retrylife.mc.remoteplayers.Database;
import ca.retrylife.mc.remoteplayers.RemotePlayers;
import ca.retrylife.mc.remoteplayers.dynmap.DynmapConnection;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;

/**
 * Contains handlers for in-game commands
 */
public class CommandSetup implements ClientCommandPlugin {

    private MinecraftClient mc;

    public CommandSetup() {
        mc = MinecraftClient.getInstance();
    }

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {

        // Command to set dynmap for a server
        dispatcher.register(ArgumentBuilders.literal("rp:enable")
                .then(ArgumentBuilders.argument("url", StringArgumentType.string()).executes(source -> {
                    // If not in a world, disallow
                    if (!RemotePlayers.isOnline) {
                        source.getSource()
                                .sendFeedback(new LiteralText("This server is not valid for use with RemotePlayers"));
                        return 0;
                    }

                    // Get the current server ip
                    String serverIP = mc.getCurrentServerEntry().address;

                    // Add to the database
                    Database.getInstance().setDynmapRemoteForServer(serverIP, source.getArgument("url", String.class));

                    // Connect to the server
                    try {
                        RemotePlayers
                                .setConnection(new DynmapConnection(new URL(source.getArgument("url", String.class))));
                    } catch (MalformedURLException e) {
                        source.getSource().sendFeedback(new LiteralText("Provided URL was malformed"));
                        return 0;
                    } catch (IOException e) {
                        source.getSource().sendFeedback(new LiteralText("Failed to contact dynmap API"));
                        e.printStackTrace();
                        return 0;
                    }

                    source.getSource().sendFeedback(new LiteralText("Added dynmap linkage"));
                    return 1;
                })));

        // Command to remove dynmap from a server
        dispatcher.register(ArgumentBuilders.literal("rp:disable").executes(source -> {

            // If not in a world, disallow
            if (!RemotePlayers.isOnline) {
                source.getSource().sendFeedback(new LiteralText("This server is not valid for use with RemotePlayers"));
                return 0;
            }

            // Get the current server ip
            String serverIP = mc.getCurrentServerEntry().address;

            // Remove the linkage
            Database.getInstance().unlinkDynmapRemoteForServer(serverIP);

            source.getSource().sendFeedback(new LiteralText("Unlinked dynmap"));
            return 1;
        }));

        dispatcher.register(ArgumentBuilders.literal("rp:reload").executes(source -> {

            // If not in a world, disallow
            if (!RemotePlayers.isOnline) {
                source.getSource().sendFeedback(new LiteralText("This server is not valid for use with RemotePlayers"));
                return 0;
            }

            RemotePlayers.setUpdateDelay(2.0);

            source.getSource().sendFeedback(new LiteralText("Reloaded"));
            return 1;
        }));

        dispatcher.register(ArgumentBuilders.literal("rp:integrate")
                .then(ArgumentBuilders.argument("enabled", BoolArgumentType.bool()).executes(source -> {

                    // If not in a world, disallow
                    if (!RemotePlayers.isOnline) {
                        source.getSource()
                                .sendFeedback(new LiteralText("This server is not valid for use with RemotePlayers"));
                        return 0;
                    }

                    // Configure
                    Database.getInstance().setEnableIntegration(source.getArgument("enabled", Boolean.class));

                    source.getSource().sendFeedback(new LiteralText("Configured setting"));
                    return 1;
                })));

    }

}