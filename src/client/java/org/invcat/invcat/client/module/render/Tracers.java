package org.invcat.invcat.client.module.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.RenderEvent;
import org.invcat.invcat.client.module.Module;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

/**
 * Draws lines to entities
 */
public class Tracers extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final boolean tracePlayers = true;
    private final boolean traceMobs = true;
    private final float lineWidth = 1.0f;
    
    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.RENDER);
        this.setKeyBind(GLFW.GLFW_KEY_T);
    }
    
    @EventHandler
    public void onRender(RenderEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) return;
        
        MatrixStack matrixStack = event.getMatrixStack();
        float partialTicks = event.getPartialTicks();
        
        // Save current render state
        matrixStack.push();
        
        // Get player position for relative rendering
        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        
        // Draw tracers
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue; // Skip rendering self
            
            if (entity instanceof PlayerEntity && tracePlayers) {
                drawTracer(matrixStack, cameraPos, entity, 1.0f, 0.0f, 0.0f, partialTicks);
            } else if (entity instanceof LivingEntity && traceMobs) {
                drawTracer(matrixStack, cameraPos, entity, 0.0f, 1.0f, 0.0f, partialTicks);
            }
        }
        
        // Restore render state
        matrixStack.pop();
    }
    
    /**
     * Draw a tracer line to an entity
     */
    private void drawTracer(MatrixStack matrixStack, Vec3d cameraPos, Entity entity, float red, float green, float blue, float partialTicks) {
        // Calculate entity position with partial ticks for smooth rendering
        double x = MathHelper.lerp(partialTicks, entity.prevX, entity.getX());
        double y = MathHelper.lerp(partialTicks, entity.prevY, entity.getY()) + entity.getHeight() / 2.0;
        double z = MathHelper.lerp(partialTicks, entity.prevZ, entity.getZ());
        
        // Draw line from player to entity
        drawLine(matrixStack, 
                0, 0, 0, // Start at camera position (already translated)
                (float)(x - cameraPos.x), (float)(y - cameraPos.y), (float)(z - cameraPos.z), // End at entity
                red, green, blue, 1.0f);
    }
    
    /**
     * Draw a line between two points
     */
    private void drawLine(MatrixStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        // Get matrix for transformations
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        
        // Get vertex consumer for lines
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lines = immediate.getBuffer(RenderLayer.getLines());
        
        // Draw line
        lines.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).normal(0, 1, 0).next();
        lines.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).normal(0, 1, 0).next();
        
        // Flush the buffer
        immediate.draw();
    }
}