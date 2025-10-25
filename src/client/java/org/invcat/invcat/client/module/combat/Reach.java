package org.invcat.invcat.client.module.combat;

import org.invcat.invcat.client.module.Module;
import org.lwjgl.glfw.GLFW;

/**
 * Increases the player's reach distance
 */
public class Reach extends Module {
    private final float reachDistance = 4.5f; // Default reach distance (vanilla is 3.0)
    
    public Reach() {
        super("Reach", "Increases your attack reach distance", Category.COMBAT);
        this.setKeyBind(GLFW.GLFW_KEY_V);
    }
    
    /**
     * Get the modified reach distance when enabled
     */
    public float getReachDistance() {
        return isEnabled() ? reachDistance : 3.0f;
    }
}