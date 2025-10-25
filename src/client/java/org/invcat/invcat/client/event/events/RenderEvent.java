package org.invcat.invcat.client.event.events;

import net.minecraft.client.util.math.MatrixStack;

/**
 * Event fired during rendering
 */
public class RenderEvent {
    private final MatrixStack matrixStack;
    private final float partialTicks;
    
    public RenderEvent(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
    }
    
    public MatrixStack getMatrixStack() {
        return matrixStack;
    }
    
    public float getPartialTicks() {
        return partialTicks;
    }
}