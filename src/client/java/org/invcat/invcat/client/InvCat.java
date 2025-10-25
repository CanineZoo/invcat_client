package org.invcat.invcat.client;

import net.minecraft.client.MinecraftClient;
import org.invcat.invcat.client.gui.ClickGUI;
import org.invcat.invcat.client.module.ModuleManager;
import org.invcat.invcat.client.event.EventManager;
import org.lwjgl.glfw.GLFW;

/**
 * Main class for the InvCat hack client
 */
public class InvCat {
    private static InvCat instance;
    private final MinecraftClient mc;
    private final ModuleManager moduleManager;
    private final EventManager eventManager;
    private final ClickGUI clickGUI;
    
    private InvCat() {
        this.mc = MinecraftClient.getInstance();
        this.moduleManager = new ModuleManager();
        this.eventManager = new EventManager();
        this.clickGUI = new ClickGUI();
    }
    
    public static InvCat getInstance() {
        if (instance == null) {
            instance = new InvCat();
        }
        return instance;
    }
    
    public void init() {
        moduleManager.init();
        System.out.println("InvCat Client initialized!");
    }
    
    public void onKeyPress(int key, int action) {
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT && action == GLFW.GLFW_PRESS) {
            mc.setScreen(clickGUI);
        }
        
        if (action == GLFW.GLFW_PRESS) {
            moduleManager.onKeyPress(key);
        }
    }
    
    public ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    public EventManager getEventManager() {
        return eventManager;
    }
    
    public MinecraftClient getMinecraft() {
        return mc;
    }
    
    public ClickGUI getClickGUI() {
        return clickGUI;
    }
}