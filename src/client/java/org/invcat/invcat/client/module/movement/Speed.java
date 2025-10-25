package org.invcat.invcat.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.MoveEvent;
import org.invcat.invcat.client.event.events.TickEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Increases player movement speed
 */
public class Speed extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final float speedMultiplier = 1.5f; // Speed multiplier
    
    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
        this.setKeyBind(GLFW.GLFW_KEY_Z);
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null || !mc.player.isOnGround()) return;
            
            // Apply speed boost when moving on ground
            if (isMoving()) {
                // Get movement direction
                float yaw = mc.player.getYaw();
                float forward = mc.player.input.movementForward;
                float strafe = mc.player.input.movementSideways;
                
                // Calculate movement angle
                float angle = yaw + (forward < 0 ? 180 : 0) + (forward == 0 ? (strafe < 0 ? 90 : (strafe > 0 ? -90 : 0)) : (strafe < 0 ? 45 : (strafe > 0 ? -45 : 0)));
                
                // Apply speed boost in the movement direction
                float speed = 0.2873f * speedMultiplier; // Base speed * multiplier
                mc.player.setVelocity(
                        -MathHelper.sin(angle * 0.017453292f) * speed,
                        mc.player.getVelocity().y,
                        MathHelper.cos(angle * 0.017453292f) * speed
                );
            }
        }
    }
    
    @EventHandler
    public void onMove(MoveEvent event) {
        if (!isEnabled() || mc.player == null) return;
        
        // Apply speed boost to movement event
        if (isMoving() && mc.player.isOnGround()) {
            double speed = Math.sqrt(event.getX() * event.getX() + event.getZ() * event.getZ());
            if (speed > 0) {
                event.setX(event.getX() / speed * 0.2873 * speedMultiplier);
                event.setZ(event.getZ() / speed * 0.2873 * speedMultiplier);
            }
        }
    }
    
    private boolean isMoving() {
        if (mc.player == null) return false;
        return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
    }
}