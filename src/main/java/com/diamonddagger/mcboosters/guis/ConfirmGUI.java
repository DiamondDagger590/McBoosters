package com.diamonddagger.mcboosters.guis;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConfirmGUI extends GUI {

  @Getter
  private String boosterType;

  private GUIInventoryFunction guiBuildFunction = (GUIBuilder builder) -> {
    FileConfiguration guiFile = McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIRM_GUI);
    Inventory inv = Bukkit.createInventory(null, guiFile.getInt("Size"), Methods.color(guiFile.getString("Title").replace("%BoosterType%", boosterType)));

    ItemStack acceptItem = new ItemStack(Material.getMaterial(guiFile.getString("AcceptItem.Material")), guiFile.getInt("AcceptItem.Amount"));
    ItemMeta acceptMeta = acceptItem.getItemMeta();
    acceptMeta.setDisplayName(Methods.color(guiFile.getString("AcceptItem.DisplayName")));
    if(guiFile.contains("AcceptItem.Lore")){
      acceptMeta.setLore(Methods.colorLore(guiFile.getStringList("AcceptItem.Lore")));
    }
    acceptItem.setItemMeta(acceptMeta);

    ItemStack denyItem = new ItemStack(Material.getMaterial(guiFile.getString("DenyItem.Material")), guiFile.getInt("DenyItem.Amount"));
    ItemMeta denyMeta = denyItem.getItemMeta();
    denyMeta.setDisplayName(Methods.color(guiFile.getString("DenyItem.DisplayName")));
    if(guiFile.contains("DenyItem.Lore")){
      denyMeta.setLore(Methods.colorLore(guiFile.getStringList("DenyItem.Lore")));
    }
    denyItem.setItemMeta(denyMeta);

    inv.setItem(guiFile.getInt("AcceptItem.Slot"), acceptItem);
    inv.setItem(guiFile.getInt("DenyItem.Slot"), denyItem);

    ItemStack filler = new ItemStack(Material.getMaterial(guiFile.getString("FillerItem.Material")), guiFile.getInt("FillerItem.Amount", 1));
    ItemMeta fillerMeta = filler.getItemMeta();
    fillerMeta.setDisplayName(guiFile.getString("FillerItem.DisplayName"));
    if(guiFile.contains("FillerItem.Lore")){
      fillerMeta.setLore(Methods.colorLore(guiFile.getStringList("FillerItem.DisplayLore")));
    }
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
