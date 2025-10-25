package org.invcat.invcat.client.event.events;

import net.minecraft.entity.Entity;

/**
 * Event fired when the player attacks an entity
 */
public class AttackEvent {
    private final Entity target;
    private boolean cancelled;
    
    public AttackEvent(Entity target) {
        this.target = target;
        this.cancelled = false;
    }
    
    public Entity getTarget() {
        return target;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}