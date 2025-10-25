package org.invcat.invcat.client.event.events;

/**
 * Event fired when the player moves
 */
public class MoveEvent {
    private double x;
    private double y;
    private double z;
    private boolean cancelled;
    
    public MoveEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.cancelled = false;
    }
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getZ() {
        return z;
    }
    
    public void setZ(double z) {
        this.z = z;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}