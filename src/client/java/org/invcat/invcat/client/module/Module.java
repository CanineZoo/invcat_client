package org.invcat.invcat.client.module;

import org.invcat.invcat.client.InvCat;
import org.invcat.invcat.client.event.EventHandler;
import org.invcat.invcat.client.event.events.TickEvent;

/**
 * Base class for all hack modules
 */
public class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private int keyBind;
    
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
        this.keyBind = -1; // No key binding by default
    }
    
    /**
     * Called when the module is enabled
     */
    public void onEnable() {
        InvCat.getInstance().getEventManager().register(this);
    }
    
    /**
     * Called when the module is disabled
     */
    public void onDisable() {
        InvCat.getInstance().getEventManager().unregister(this);
    }
    
    /**
     * Toggle the module on/off
     */
    public void toggle() {
        setEnabled(!enabled);
    }
    
    /**
     * Set the enabled state of the module
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            
            if (enabled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }
    
    /**
     * Default tick handler for modules
     */
    @EventHandler
    public void onTick(TickEvent event) {
        if (event.getPhase() == TickEvent.Phase.END && enabled) {
            onUpdate();
        }
    }
    
    /**
     * Called every tick when the module is enabled
     */
    public void onUpdate() {
        // Override in subclasses
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public int getKeyBind() {
        return keyBind;
    }
    
    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }
    
    /**
     * Module categories
     */
    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        RENDER("Render"),
        PLAYER("Player"),
        WORLD("World"),
        MISC("Misc");
        
        private final String name;
        
        Category(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
}