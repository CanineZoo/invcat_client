package org.invcat.invcat.client.module.movement;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.TickEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Prevents fall damage
 */
public class NoFall extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT);
        this.setKeyBind(GLFW.GLFW_KEY_N);
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null || mc.getNetworkHandler() == null) return;
            
            // Prevent fall damage by sending onGround=true packets when falling
            if (mc.player.fallDistance > 2.0f) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            }
        }
    }
}