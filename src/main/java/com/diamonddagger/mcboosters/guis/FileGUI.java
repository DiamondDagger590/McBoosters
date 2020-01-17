package com.diamonddagger.mcboosters.guis;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.Methods;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FileGUI extends GUI {

  @Getter
  private String name;

  /*
  public FileGUI(String name, FileConfiguration file, BoosterPlayer player){
    super(new GUIBuilder(name, file, player));
    this.getGui().replacePlaceHolders();
    this.name = name;
    if(!GUITracker.isPlayerTracked(player)){
      GUITracker.trackPlayer(player, this);
    }
  }*/

  public FileGUI(String name, FileConfiguration file, BoosterPlayer player){
    super(new GUIBuilder(name, file, player));
    this.name = name;
    if(!GUITracker.isPlayerTracked(player)){
      GUITracker.trackPlayer(player, this);
    }

    GUIPlaceHolderFunction placeHolderFunction = (GUIBuilder builder) -> {
      int i = 0;
      for(ItemStack item : builder.getInv().getContents()){
        if(item.hasItemMeta()){
          ItemMeta meta = item.getItemMeta();
          if(meta != null && meta.hasLore()){
            String boosterName = "";
            for(String s : file.getConfigurationSection("Gui." + name + ".Items").getKeys(false)){
              String key = "Gui." + name + ".Items." + s + ".";
              if(file.getInt(key + "Slot") == i){
                if(file.contains(key + "BoosterDisplay")){
                  boosterName = file.getString(key + "BoosterDisplay");
                }
              }
            }
            if(boosterName.equals("")){
              continue;
            }
            List<String> newLore = new ArrayList<>();
            for(String s : meta.getLore()){
              newLore.add(s.replace("%" + boosterName + "Amount%", Integer.toString(player.getBoosterAmount(boosterName)))
                      .replace("%Active" + boosterName + "s%", Integer.toString(McBoosters.getInstance().getBoosterManager().getActiveBoosters(boosterName).size()))
                      .replace("%Possible" + boosterName + "s%", Integer.toString(McBoosters.getInstance().getBoosterManager().getBoosterInfo(boosterName).getMaxAmount()))
                      .replace("%Next" + boosterName + "EndTime%", Methods.convertMilisRemainder(Methods.color("H: %Hour% M: %Minute% S: %Second%")
                              , McBoosters.getInstance().getBoosterManager().getNextEndingBooster(boosterName))));
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
          }
        }
        i++;
      }
    };
    this.getGui().setReplacePlaceHoldersFunction(placeHolderFunction);
    this.getGui().replacePlaceHolders();
    player.getPlayer().updateInventory();
  }

}
