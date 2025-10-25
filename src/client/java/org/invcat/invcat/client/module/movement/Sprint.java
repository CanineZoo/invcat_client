package org.invcat.invcat.client.module.movement;

import net.minecraft.client.MinecraftClient;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.TickEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Automatically sprints when moving forward
 */
public class Sprint extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final boolean omniSprint = true; // Sprint in all directions, not just forward
    
    public Sprint() {
        super("Sprint", "Automatically sprints when moving", Category.MOVEMENT);
        this.setKeyBind(GLFW.GLFW_KEY_M);
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null) return;
            
            // Check if player can sprint
            if (canSprint()) {
                mc.player.setSprinting(true);
            }
        }
    }
    
    private boolean canSprint() {
        if (mc.player == null) return false;
        
        // Check if player can sprint
        return !mc.player.isSneaking() 
                && !mc.player.isUsingItem() 
                && mc.player.getHungerManager().getFoodLevel() > 6 
                && (omniSprint ? isMoving() : mc.player.input.movementForward > 0);
    }
    
    private boolean isMoving() {
        if (mc.player == null) return false;
        return mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0;
    }
}