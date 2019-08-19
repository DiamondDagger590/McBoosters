package com.diamonddagger.mcboosters.guis;

import com.diamonddagger.mcboosters.util.Methods;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGUI extends GUI {

  @Getter
  private String boosterType;

  private GUIInventoryFunction guiBuildFunction = (GUIBuilder builder) -> {
    Inventory inv = Bukkit.createInventory(null, 27, Methods.color("&aConfirm activation of " + boosterType));

    ItemStack acceptItem = new ItemStack(Material.EMERALD_BLOCK, 1);
    ItemMeta acceptMeta = acceptItem.getItemMeta();
    acceptMeta.setDisplayName(Methods.color("&aClick this to activate a booster"));
    acceptItem.setItemMeta(acceptMeta);

    ItemStack denyItem = new ItemStack(Material.REDSTONE_BLOCK, 1);
    ItemMeta denyMeta = denyItem.getItemMeta();
    denyMeta.setDisplayName(Methods.color("&aClick this to cancel booster activation"));
    denyItem.setItemMeta(denyMeta);

    inv.setItem(11, acceptItem);
    inv.setItem(15, denyItem);

    ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    ItemMeta fillerMeta = filler.getItemMeta();
    fillerMeta.setDisplayName(" ");
    filler.setItemMeta(fillerMeta);

    for(int i = 0; i < inv.getSize(); i++){
      if(inv.getItem(i) == null){
        inv.setItem(i, filler);
      }
    }
    return inv;
  };

  public ConfirmGUI(String boosterType, GUIBuilder gui){
    super(gui);
    this.boosterType = boosterType;

    gui.setBuildGUIFunction(guiBuildFunction);
    gui.rebuildGUI();
  }
}
