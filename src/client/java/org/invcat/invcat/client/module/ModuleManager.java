package org.invcat.invcat.client.module;

import org.invcat.invcat.client.module.combat.*;
import org.invcat.invcat.client.module.movement.*;
import org.invcat.invcat.client.module.render.*;
import org.invcat.invcat.client.module.player.*;
import org.invcat.invcat.client.module.world.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all hack modules
 */
public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();
    private final Map<Module.Category, List<Module>> modulesByCategory = new HashMap<>();
    
    public ModuleManager() {
        // Initialize category lists
        for (Module.Category category : Module.Category.values()) {
            modulesByCategory.put(category, new ArrayList<>());
        }
    }
    
    /**
     * Initialize all modules
     */
    public void init() {
        // Combat modules
        registerModule(new KillAura());
        registerModule(new Reach());
        registerModule(new Aimbot());
        registerModule(new Criticals());
        registerModule(new AntiKnockback());
        
        // Movement modules
        registerModule(new Speed());
        registerModule(new Flight());
        registerModule(new Sprint());
        registerModule(new NoFall());
        registerModule(new Jesus());
        
        // Render modules
        registerModule(new ESP());
        registerModule(new Tracers());
        registerModule(new Fullbright());
        registerModule(new Xray());
        registerModule(new NameTags());
        
        // Player modules
        registerModule(new AutoTool());
        registerModule(new Scaffold());
        registerModule(new FastPlace());
        registerModule(new NoSlow());
        
        // World modules
        registerModule(new Nuker());
        registerModule(new ChestESP());
        
        System.out.println("Loaded " + modules.size() + " modules");
    }
    
    /**
     * Register a module
     */
    private void registerModule(Module module) {
        modules.add(module);
        modulesByCategory.get(module.getCategory()).add(module);
    }
    
    /**
     * Get all modules
     */
    public List<Module> getModules() {
        return modules;
    }
    
    /**
     * Get modules by category
     */
    public List<Module> getModulesByCategory(Module.Category category) {
        return modulesByCategory.get(category);
    }
    
    /**
     * Get a module by name
     */
    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }
    
    /**
     * Handle key press
     */
    public void onKeyPress(int key) {
        for (Module module : modules) {
            if (module.getKeyBind() == key) {
                module.toggle();
            }
        }
    }
}