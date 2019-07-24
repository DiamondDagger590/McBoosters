package com.diamonddagger.mcboosters.guis;

import org.bukkit.inventory.Inventory;

@FunctionalInterface
public interface GUIInventoryFunction {

  Inventory generateInventory(GUIBuilder builder);
}
