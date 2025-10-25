package org.invcat.invcat.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.MoveEvent;
import org.invcat.invcat.client.event.events.TickEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Allows walking on water
 */
public class Jesus extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int tickTimer = 0;
    
    public Jesus() {
        super("Jesus", "Allows you to walk on water", Category.MOVEMENT);
        this.setKeyBind(GLFW.GLFW_KEY_J);
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null || mc.world == null) return;
            
            tickTimer++;
            
            // Check if player is in water or lava
            if (isInLiquid()) {
                // Push player up when in liquid
                if (mc.player.input.jumping) {
                    mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
                } else {
                    mc.player.setVelocity(mc.player.getVelocity().x, 0.1, mc.player.getVelocity().z);
                }
            }
            
            // Simulate bouncing on water surface
            if (isOnLiquid() && !mc.player.input.jumping && !isInLiquid()) {
                // Bounce effect every 20 ticks
                if (tickTimer % 20 == 0) {
                    mc.player.setVelocity(mc.player.getVelocity().x, -0.02, mc.player.getVelocity().z);
                }
            }
        }
    }
    
    @EventHandler
    public void onMove(MoveEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) return;
        
        // Prevent sinking when standing on liquid
        if (isOnLiquid() && !isInLiquid() && !mc.player.input.jumping) {
            event.setY(0);
        }
    }
    
    /**
     * Check if player is in a liquid (water or lava)
     */
    private boolean isInLiquid() {
        if (mc.player == null || mc.world == null) return false;
        
        // Get player bounding box
        Box box = mc.player.getBoundingBox().contract(0.1);
        
        // Check blocks in bounding box for liquids
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
                for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    FluidState fluidState = mc.world.getFluidState(pos);
                    
                    if (!fluidState.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if player is on top of a liquid
     */
    private boolean isOnLiquid() {
        if (mc.player == null || mc.world == null) return false;
        
        // Check for liquid below player
        Box box = mc.player.getBoundingBox().offset(0, -0.1, 0).contract(0.1);
        
        for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
            for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
                BlockPos pos = new BlockPos(x, (int) Math.floor(box.minY), z);
                FluidState fluidState = mc.world.getFluidState(pos);
                
                if (!fluidState.isEmpty()) {
                    return true;
                }
            }
        }
        
        return false;
    }
}