package org.invcat.invcat.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.PacketEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Reduces or cancels knockback from attacks and explosions
 */
public class AntiKnockback extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final float horizontal = 0.0f; // Horizontal knockback multiplier (0.0 = no knockback)
    private final float vertical = 0.0f; // Vertical knockback multiplier (0.0 = no knockback)
    
    public AntiKnockback() {
        super("AntiKnockback", "Reduces or cancels knockback", Category.COMBAT);
        this.setKeyBind(GLFW.GLFW_KEY_K);
    }
    
    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!isEnabled() || mc.player == null) return;
        
        if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket packet) {
            // Check if the packet is for the player
            if (packet.getId() == mc.player.getId()) {
                if (horizontal == 0 && vertical == 0) {
                    event.setCancelled(true);
                } else {
                    // Modify the velocity values
                    // This would require a mixin to modify the packet
                }
            }
        } else if (event.getPacket() instanceof ExplosionS2CPacket) {
            // Cancel explosion knockback
            event.setCancelled(true);
        }
    }
}