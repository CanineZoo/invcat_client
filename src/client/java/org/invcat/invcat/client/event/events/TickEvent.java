package org.invcat.invcat.client.event.events;

/**
 * Event fired each game tick
 */
public class TickEvent {
    private final Phase phase;
    
    public TickEvent(Phase phase) {
        this.phase = phase;
    }
    
    public Phase getPhase() {
        return phase;
    }
    
    public enum Phase {
        START, END
    }
}