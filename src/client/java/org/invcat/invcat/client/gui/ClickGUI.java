package org.invcat.invcat.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.invcat.invcat.client.InvCat;
import org.invcat.invcat.client.module.Module;

import java.util.ArrayList;
import java.util.List;

/**
 * ClickGUI for the hack client
 */
public class ClickGUI extends Screen {
    private final List<CategoryPanel> panels = new ArrayList<>();
    private CategoryPanel draggingPanel = null;
    private int dragX, dragY;
    
    public ClickGUI() {
        super(Text.literal("InvCat Client"));
        
        // Create panels for each category
        int x = 10;
        for (Module.Category category : Module.Category.values()) {
            panels.add(new CategoryPanel(category, x, 10));
            x += 120; // Space between panels
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw a semi-transparent background
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, "InvCat Client", this.width / 2, 5, 0xFFFFFFFF);
        
        // Draw all panels
        for (CategoryPanel panel : panels) {
            panel.render(context, mouseX, mouseY);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if a panel header was clicked for dragging
        if (button == 0) { // Left click
            for (CategoryPanel panel : panels) {
                if (panel.isHeaderHovered((int) mouseX, (int) mouseY)) {
                    draggingPanel = panel;
                    dragX = (int) mouseX - panel.getX();
                    dragY = (int) mouseY - panel.getY();
                    return true;
                }
            }
        }
        
        // Check for module clicks
        for (CategoryPanel panel : panels) {
            if (panel.mouseClicked((int) mouseX, (int) mouseY, button)) {
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggingPanel != null) {
            draggingPanel = null;
            return true;
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggingPanel != null) {
            draggingPanel.setPosition((int) mouseX - dragX, (int) mouseY - dragY);
            return true;
        }
        
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game when GUI is open
    }
    
    /**
     * Panel for a category of modules
     */
    private class CategoryPanel {
        private final Module.Category category;
        private int x, y;
        private int width = 100;
        private int headerHeight = 20;
        private boolean expanded = true;
        private final List<Module> modules;
        
        public CategoryPanel(Module.Category category, int x, int y) {
            this.category = category;
            this.x = x;
            this.y = y;
            this.modules = InvCat.getInstance().getModuleManager().getModulesByCategory(category);
        }
        
        public void render(DrawContext context, int mouseX, int mouseY) {
            // Draw panel header
            int headerColor = isHeaderHovered(mouseX, mouseY) ? 0xFF3030FF : 0xFF1010AA;
            context.fill(x, y, x + width, y + headerHeight, headerColor);
            context.drawCenteredTextWithShadow(textRenderer, category.getName(), x + width / 2, y + 6, 0xFFFFFFFF);
            
            // Draw expand/collapse indicator
            String indicator = expanded ? "-" : "+";
            context.drawTextWithShadow(textRenderer, indicator, x + width - 10, y + 6, 0xFFFFFFFF);
            
            if (expanded) {
                // Draw module buttons
                int moduleY = y + headerHeight;
                for (Module module : modules) {
                    // Module background
                    int moduleColor = module.isEnabled() ? 0xFF20AA20 : 0xFF202020;
                    if (isModuleHovered(mouseX, mouseY, moduleY)) {
                        moduleColor = module.isEnabled() ? 0xFF30FF30 : 0xFF404040;
                    }
                    context.fill(x, moduleY, x + width, moduleY + 20, moduleColor);
                    
                    // Module name
                    context.drawTextWithShadow(textRenderer, module.getName(), x + 5, moduleY + 6, 0xFFFFFFFF);
                    
                    moduleY += 20;
                }
            }
        }
        
        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            // Check header click for expand/collapse
            if (isHeaderHovered(mouseX, mouseY)) {
                if (button == 1) { // Right click
                    expanded = !expanded;
                    return true;
                }
                return false; // Let the main class handle dragging
            }
            
            // Check module clicks
            if (expanded && mouseX >= x && mouseX <= x + width) {
                int moduleY = y + headerHeight;
                for (Module module : modules) {
                    if (mouseY >= moduleY && mouseY <= moduleY + 20) {
                        if (button == 0) { // Left click to toggle
                            module.toggle();
                            return true;
                        }
                    }
                    moduleY += 20;
                }
            }
            
            return false;
        }
        
        public boolean isHeaderHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight;
        }
        
        public boolean isModuleHovered(int mouseX, int mouseY, int moduleY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= moduleY && mouseY <= moduleY + 20;
        }
        
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }
}