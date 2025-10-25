package org.invcat.invcat.client.event;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages event subscriptions and dispatching
 */
public class EventManager {
    private final Map<Class<?>, List<EventListener>> listeners = new HashMap<>();
    
    /**
     * Register an object with event listener methods
     */
    public void register(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class) && method.getParameterCount() == 1) {
                Class<?> eventType = method.getParameterTypes()[0];
                
                if (!listeners.containsKey(eventType)) {
                    listeners.put(eventType, new ArrayList<>());
                }
                
                listeners.get(eventType).add(new EventListener(object, method));
            }
        }
    }
    
    /**
     * Unregister an object from receiving events
     */
    public void unregister(Object object) {
        for (List<EventListener> eventListeners : listeners.values()) {
            eventListeners.removeIf(listener -> listener.getObject() == object);
        }
    }
    
    /**
     * Post an event to all registered listeners
     */
    public void post(Object event) {
        List<EventListener> eventListeners = listeners.get(event.getClass());
        
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.invoke(event);
            }
        }
    }
    
    /**
     * Class to hold event listener information
     */
    private static class EventListener {
        private final Object object;
        private final Method method;
        
        public EventListener(Object object, Method method) {
            this.object = object;
            this.method = method;
            this.method.setAccessible(true);
        }
        
        public Object getObject() {
            return object;
        }
        
        public void invoke(Object event) {
            try {
                method.invoke(object, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}