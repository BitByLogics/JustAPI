package net.bitbylogic.apibylogic.menu;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.bitbylogic.apibylogic.menu.action.MenuClickActionType;
import net.bitbylogic.apibylogic.menu.action.MenuClickRequirement;
import net.bitbylogic.apibylogic.menu.view.MenuViewRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class MenuItem implements Cloneable {

    private final String identifier;

    private ItemStack item;
    private MenuItemUpdateProvider itemUpdateProvider;

    private ConfigurationSection itemSection;
    private List<Inventory> sourceInventories = new ArrayList<>();
    private List<Integer> slots;
    private HashMap<String, String> metaData;
    private boolean updatable;
    private List<MenuAction> actions;
    private HashMap<MenuClickActionType, String> internalActions;
    private List<MenuClickRequirement> clickRequirements;
    private List<MenuViewRequirement> viewRequirements;

    public MenuItem(String identifier, ItemStack item, List<Integer> slots, boolean updatable, MenuAction action, HashMap<MenuClickActionType, String> internalActions) {
        this(identifier, item, slots, updatable);
        this.actions.add(action);
        this.internalActions = internalActions;
    }

    public MenuItem(String identifier, ItemStack item, List<Integer> slots, boolean updatable, MenuAction action) {
        this(identifier, item, slots, updatable);
        this.actions.add(action);
        this.internalActions = new HashMap<>();
    }

    public MenuItem(String identifier, ItemStack item, List<Integer> slots, boolean updatable, List<MenuAction> actions) {
        this(identifier, item, slots, updatable);
        this.actions = actions;
        this.internalActions = new HashMap<>();
    }

    public MenuItem(String identifier, ItemStack item, List<Integer> slots, boolean updatable) {
        this.identifier = identifier;
        this.item = item;
        this.slots = slots;
        this.updatable = updatable;
        this.actions = new ArrayList<>();
        this.internalActions = new HashMap<>();
        this.metaData = new HashMap<>();
        this.clickRequirements = new ArrayList<>();
        this.viewRequirements = new ArrayList<>();
    }

    public MenuItem addSlot(int slot) {
        slots.add(slot);
        return this;
    }

    public MenuItem addAction(MenuAction action) {
        actions.add(action);
        return this;
    }

    public MenuItem addClickRequirement(MenuClickRequirement requirement) {
        clickRequirements.add(requirement);
        return this;
    }

    public MenuItem addViewRequirement(MenuViewRequirement requirement) {
        viewRequirements.add(requirement);
        return this;
    }

    public void addSourceInventory(Inventory inventory) {
        sourceInventories.add(inventory);
    }

    public MenuAction getAction() {
        return actions.get(0);
    }

    public void onClick(InventoryClickEvent event) {
        if (clickRequirements.stream().anyMatch(requirement -> !requirement.canClick((Player) event.getWhoClicked()))) {
            return;
        }

        internalActions.keySet().forEach(action -> action.getAction().onClick(event, internalActions.get(action)));
        actions.forEach(action -> action.onClick(event));
    }

    public MenuItem clone() {
        return clone(true);
    }

    public MenuItem clone(boolean cloneAction) {
        return new MenuItem(identifier, item.clone(), new ArrayList<>(slots), updatable, cloneAction ? new ArrayList<>(actions) : Lists.newArrayList());
    }
}
