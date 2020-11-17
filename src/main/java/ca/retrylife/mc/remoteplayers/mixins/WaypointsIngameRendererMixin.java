package ca.retrylife.mc.remoteplayers.mixins;

import java.util.Collection;
import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xaero.common.AXaeroMinimap;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.render.WaypointsIngameRenderer;

@Mixin(WaypointsIngameRenderer.class)
public abstract class WaypointsIngameRendererMixin extends WaypointsIngameRenderer{

    private AXaeroMinimap modMain;

    public WaypointsIngameRendererMixin(AXaeroMinimap modMain, MinecraftClient mc) {
        super(modMain, mc);
        this.modMain = modMain;
     }

    @Accessor
    public abstract void renderWaypointIngame(float cameraAngleYaw, Vec3d lookVector, Waypoint w, AXaeroMinimap modMain,
            double radius, double d3, double d4, double d5, Entity entity, BufferBuilder bufferBuilder,
            Tessellator tessellator, double dimDiv, double actualEntityY, float yaw, float pitch);

    private void renderWaypointsList(Collection<Waypoint> list, double d3, double d4, double d5, Entity entity,
            BufferBuilder bufferbuilder, Tessellator tessellator, double dimDiv, double actualEntityY, float yaw,
            float pitch) {
        synchronized (list) {
            float cameraAngleYaw = MathHelper.wrapDegrees(entity.yaw);
            Vec3d lookVector = entity.getRotationVector();
            Iterator<Waypoint> iter = list.iterator();

            while (iter.hasNext()) {
                Waypoint w = (Waypoint) iter.next();
                renderWaypointIngame(cameraAngleYaw, lookVector, w, modMain, 12.0D, d3, d4, d5, entity,
                        bufferbuilder, tessellator, dimDiv, actualEntityY, yaw, pitch);
            }
        }
    }

}