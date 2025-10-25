package org.invcat.invcat.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.TickEvent;
import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Automatically attacks entities around the player
 */
public class KillAura extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private int delay = 0;
    private final int maxDelay = 10; // Attack delay in ticks
    private final float range = 4.0f; // Attack range
    private final boolean rotateToTarget = true; // Whether to rotate to the target
    
    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT);
        this.setKeyBind(GLFW.GLFW_KEY_R);
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        delay = 0;
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null || mc.world == null) return;
            
            delay++;
            if (delay >= maxDelay) {
                delay = 0;
                attack();
            }
        }
    }
    
    private void attack() {
        if (mc.player == null || mc.world == null) return;
        
        // Find valid targets
        List<Entity> targets = mc.world.getEntities().stream()
                .filter(entity -> entity instanceof LivingEntity && entity != mc.player)
                .filter(entity -> entity.isAlive())
                .filter(entity -> mc.player.distanceTo(entity) <= range)
                .filter(entity -> !(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()))
                .sorted(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity)))
                .collect(Collectors.toList());
        
        if (!targets.isEmpty()) {
            Entity target = targets.get(0);
            
            // Rotate to target if enabled
            if (rotateToTarget) {
                rotateToEntity(target);
            }
            
            // Attack the target
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
    
    private void rotateToEntity(Entity entity) {
        if (mc.player == null) return;
        
        Vec3d eyePos = mc.player.getEyePos();
        Box targetBox = entity.getBoundingBox();
        Vec3d targetPos = new Vec3d(
                targetBox.minX + (targetBox.maxX - targetBox.minX) * 0.5,
                targetBox.minY + (targetBox.maxY - targetBox.minY) * 0.5,
                targetBox.minZ + (targetBox.maxZ - targetBox.minZ) * 0.5
        );
        
        double diffX = targetPos.x - eyePos.x;
        double diffY = targetPos.y - eyePos.y;
        double diffZ = targetPos.z - eyePos.z;
        
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        
        // Smooth rotation
        mc.player.setYaw(MathHelper.wrapDegrees(yaw));
        mc.player.setPitch(MathHelper.clamp(MathHelper.wrapDegrees(pitch), -90.0F, 90.0F));
    }
}