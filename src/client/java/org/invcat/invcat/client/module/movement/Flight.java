package org.invcat.invcat.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.TickEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Allows the player to fly
 */
public class Flight extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final float flySpeed = 0.8f; // Flying speed
    private boolean wasFlying;
    private boolean wasAllowFlying;
    
    public Flight() {
        super("Flight", "Allows you to fly", Category.MOVEMENT);
        this.setKeyBind(GLFW.GLFW_KEY_F);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            // Save current flying state
            wasFlying = mc.player.getAbilities().flying;
            wasAllowFlying = mc.player.getAbilities().allowFlying;
            
            // Enable flying
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed(flySpeed);
        }
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            // Restore previous flying state
            mc.player.getAbilities().allowFlying = wasAllowFlying;
            mc.player.getAbilities().flying = wasFlying && wasAllowFlying;
            mc.player.getAbilities().setFlySpeed(0.05f); // Default fly speed
            
            // Reset velocity to prevent fall damage
            if (!mc.player.getAbilities().flying) {
                mc.player.setVelocity(0, 0, 0);
            }
        }
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null) return;
            
            ClientPlayerEntity player = mc.player;
            
            // Ensure flying is enabled
            if (!player.getAbilities().flying) {
                player.getAbilities().flying = true;
            }
            
            // Set fly speed
            player.getAbilities().setFlySpeed(flySpeed);
        }
    }
}