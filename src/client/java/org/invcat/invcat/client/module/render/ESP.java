package org.invcat.invcat.client.module.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.RenderEvent;
import org.invcat.invcat.client.module.Module;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

/**
 * Renders entity outlines through walls
 */
public class ESP extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final boolean renderPlayers = true;
    private final boolean renderMobs = true;
    private final float lineWidth = 2.0f;
    
    public ESP() {
        super("ESP", "Shows entities through walls", Category.RENDER);
        this.setKeyBind(GLFW.GLFW_KEY_H);
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
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        // Render entities
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue; // Skip rendering self
            
            if (entity instanceof PlayerEntity && renderPlayers) {
                renderEntityBox(matrixStack, entity, 1.0f, 0.0f, 0.0f, partialTicks);
            } else if (entity instanceof LivingEntity && renderMobs) {
                renderEntityBox(matrixStack, entity, 0.0f, 1.0f, 0.0f, partialTicks);
            }
        }
        
        // Restore render state
        matrixStack.pop();
    }
    
    /**
     * Render a box around an entity
     */
    private void renderEntityBox(MatrixStack matrixStack, Entity entity, float red, float green, float blue, float partialTicks) {
        // Calculate entity position with partial ticks for smooth rendering
        double x = MathHelper.lerp(partialTicks, entity.prevX, entity.getX());
        double y = MathHelper.lerp(partialTicks, entity.prevY, entity.getY());
        double z = MathHelper.lerp(partialTicks, entity.prevZ, entity.getZ());
        
        // Get entity bounding box
        Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ())
                .offset(x, y, z);
        
        // Draw box outline
        drawBoxOutline(matrixStack, box, red, green, blue, 1.0f);
    }
    
    /**
     * Draw a box outline
     */
    private void drawBoxOutline(MatrixStack matrixStack, Box box, float red, float green, float blue, float alpha) {
        // Get matrix for transformations
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        
        // Get vertex consumer for lines
        VertexConsumerProvider.Immediate immediate = mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer lines = immediate.getBuffer(RenderLayer.getLines());
        
        // Draw box edges
        // Bottom face
        drawLine(matrix, lines, (float)box.minX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.minY, (float)box.minZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.maxX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.minY, (float)box.maxZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.maxX, (float)box.minY, (float)box.maxZ, (float)box.minX, (float)box.minY, (float)box.maxZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.minX, (float)box.minY, (float)box.maxZ, (float)box.minX, (float)box.minY, (float)box.minZ, red, green, blue, alpha);
        
        // Top face
        drawLine(matrix, lines, (float)box.minX, (float)box.maxY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.minZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.maxX, (float)box.maxY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.maxX, (float)box.maxY, (float)box.maxZ, (float)box.minX, (float)box.maxY, (float)box.maxZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.minX, (float)box.maxY, (float)box.maxZ, (float)box.minX, (float)box.maxY, (float)box.minZ, red, green, blue, alpha);
        
        // Connecting lines
        drawLine(matrix, lines, (float)box.minX, (float)box.minY, (float)box.minZ, (float)box.minX, (float)box.maxY, (float)box.minZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.maxX, (float)box.minY, (float)box.minZ, (float)box.maxX, (float)box.maxY, (float)box.minZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.maxX, (float)box.minY, (float)box.maxZ, (float)box.maxX, (float)box.maxY, (float)box.maxZ, red, green, blue, alpha);
        drawLine(matrix, lines, (float)box.minX, (float)box.minY, (float)box.maxZ, (float)box.minX, (float)box.maxY, (float)box.maxZ, red, green, blue, alpha);
        
        // Flush the buffer
        immediate.draw();
    }
    
    /**
     * Draw a line between two points
     */
    private void drawLine(Matrix4f matrix, VertexConsumer buffer, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).normal(0, 1, 0).next();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).normal(0, 1, 0).next();
    }
}