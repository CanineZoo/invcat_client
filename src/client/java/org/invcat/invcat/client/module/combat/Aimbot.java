package org.invcat.invcat.client.module.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
 * Automatically aims at nearby entities
 */
public class Aimbot extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final float range = 6.0f; // Aim range
    private final float speed = 0.5f; // Aim speed (0-1)
    private final boolean aimPlayers = true; // Whether to aim at players
    private final boolean aimMobs = true; // Whether to aim at mobs
    
    public Aimbot() {
        super("Aimbot", "Automatically aims at nearby entities", Category.COMBAT);
        this.setKeyBind(GLFW.GLFW_KEY_B);
    }
    
    @Override
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && isEnabled()) {
            if (mc.player == null || mc.world == null) return;
            
            // Only aim when holding attack button
            if (!mc.options.attackKey.isPressed()) return;
            
            // Find valid targets
            List<Entity> targets = mc.world.getEntities().stream()
                    .filter(entity -> entity instanceof LivingEntity && entity != mc.player)
                    .filter(entity -> entity.isAlive())
                    .filter(entity -> mc.player.distanceTo(entity) <= range)
                    .filter(entity -> {
                        if (entity instanceof PlayerEntity) {
                            return aimPlayers && !((PlayerEntity) entity).isCreative();
                        }
                        return aimMobs;
                    })
                    .sorted(Comparator.comparingDouble(entity -> mc.player.distanceTo(entity)))
                    .collect(Collectors.toList());
            
            if (!targets.isEmpty()) {
                Entity target = targets.get(0);
                aimAtEntity(target);
            }
        }
    }
    
    private void aimAtEntity(Entity entity) {
        if (mc.player == null) return;
        
        Vec3d eyePos = mc.player.getEyePos();
        Box targetBox = entity.getBoundingBox();
        
        // Aim at the center of the entity's hitbox
        Vec3d targetPos = new Vec3d(
                targetBox.minX + (targetBox.maxX - targetBox.minX) * 0.5,
                targetBox.minY + (targetBox.maxY - targetBox.minY) * 0.5,
                targetBox.minZ + (targetBox.maxZ - targetBox.minZ) * 0.5
        );
        
        double diffX = targetPos.x - eyePos.x;
        double diffY = targetPos.y - eyePos.y;
        double diffZ = targetPos.z - eyePos.z;
        
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        
        float targetYaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        
        // Smooth rotation based on aim speed
        float yaw = MathHelper.wrapDegrees(targetYaw);
        float pitch = MathHelper.clamp(MathHelper.wrapDegrees(targetPitch), -90.0F, 90.0F);
        
        float currentYaw = mc.player.getYaw();
        float currentPitch = mc.player.getPitch();
        
        float deltaYaw = MathHelper.wrapDegrees(yaw - currentYaw);
        float deltaPitch = pitch - currentPitch;
        
        float newYaw = currentYaw + deltaYaw * speed;
        float newPitch = currentPitch + deltaPitch * speed;
        
        mc.player.setYaw(newYaw);
        mc.player.setPitch(MathHelper.clamp(newPitch, -90.0F, 90.0F));
    }
}