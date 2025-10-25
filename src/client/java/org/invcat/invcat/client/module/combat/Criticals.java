package org.invcat.invcat.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.AttackEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Automatically performs critical hits
 */
public class Criticals extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    public Criticals() {
        super("Criticals", "Automatically performs critical hits", Category.COMBAT);
        this.setKeyBind(GLFW.GLFW_KEY_C);
    }
    
    @EventHandler
    public void onAttack(AttackEvent event) {
        if (!isEnabled() || mc.player == null || mc.player.isInLava() || mc.player.isTouchingWater() || 
                mc.player.isClimbing() || !mc.player.isOnGround()) {
            return;
        }
        
        // Perform a critical hit by sending small jump packets
        doCritical();
    }
    
    private void doCritical() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        
        // Send position packets to simulate a tiny jump
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.11, z, false));
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.1100013579, z, false));
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0000013579, z, false));
    }
}