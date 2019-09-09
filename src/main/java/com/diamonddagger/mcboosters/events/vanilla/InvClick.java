package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.guis.*;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;

public class InvClick implements Listener {

  @EventHandler
  private void invClick(InventoryClickEvent e){
    Player p = (Player) e.getWhoClicked();
    FileConfiguration langFile = McBoosters.getInstance().getLangFile();
    //If this is a gui
    if(GUITracker.isPlayerTracked(p)){
      //Cancel event
      e.setCancelled(true);
      //Ignore player inventory
      if(e.getClickedInventory() instanceof PlayerInventory){
        return;
      }
      BoosterPlayer mp = McBoosters.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
      //Cuz null errors are fun
      if(e.getCurrentItem() == null){
        return;
      }
      GUI currentGUI = GUITracker.getPlayersGUI(p);
      GUIEventBinder binder = null;
      if(currentGUI instanceof ConfirmGUI){
        ConfirmGUI confirmGUI = (ConfirmGUI) currentGUI;
        String boosterType = confirmGUI.getBoosterType();
        //Confirm button
        if(e.getSlot() == 11){
          p.closeInventory();
          mp.decrementBoosterAmount(boosterType, 1);
          McBoosters.getInstance().getBoosterManager().activateBooster(mp, boosterType);
          return;
        }
        //Close button
        else if(e.getSlot() == 15){
          p.closeInventory();
          return;
        }
      }
      if(currentGUI.getGui().getBoundEvents() != null){
        binder = currentGUI.getGui().getBoundEvents().stream().filter(guiBinder -> guiBinder.getSlot() == e.getSlot()).findFirst().orElse(null);
      }
      if(binder == null){
        return;
      }
      for(String eventBinder : binder.getBoundEventList()){
        String[] events = eventBinder.split(":");
        String event = events[0];
        if(event.equalsIgnoreCase("Permission")){
          String perm = events[1];
          if(!p.hasPermission(perm)){
            GUITracker.stopTrackingPlayer(p);
            p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + langFile.getString("Messages.Commands.Utility.NoPerms")));
            return;
          }
          else{
            continue;
          }
        }
        else if(event.equalsIgnoreCase("Command")){
          String sender = events[1];
          if(sender.equalsIgnoreCase("console")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), events[2].replace("%Player%", p.getName()));
            continue;
          }
          else if(sender.equalsIgnoreCase("player")){
            p.performCommand(events[2]);
            continue;
          }
        }
        else if(event.equalsIgnoreCase("close")){
          GUITracker.stopTrackingPlayer(p);
          p.closeInventory();
          continue;
        }
        else if(event.equalsIgnoreCase("back")){
          if(GUITracker.doesPlayerHavePrevious(p)){
            GUI previousGUI = GUITracker.getPlayersPreviousGUI(p);
            previousGUI.setClearData(true);
            currentGUI.setClearData(false);
            p.openInventory(previousGUI.getGui().getInv());
            GUITracker.replacePlayersGUI(p, previousGUI);
            continue;
          }
          else{
            GUITracker.stopTrackingPlayer(p);
            continue;
          }
        }
        else if(event.equalsIgnoreCase("Open")){
          String name = events[1];
          GUI gui = new FileGUI(name, currentGUI.getGui().getConfig(), mp);
          currentGUI.setClearData(false);
          p.openInventory(gui.getGui().getInv());
          GUITracker.replacePlayersGUI(mp, gui);
          return;
        }
        else if(event.equalsIgnoreCase("OpenFile")){
          String name = events[2];
          File file = new File(McBoosters.getInstance().getDataFolder(), File.separator + events[1]);
          GUI gui = new FileGUI(name, YamlConfiguration.loadConfiguration(file), mp);
          currentGUI.setClearData(false);
          p.openInventory(gui.getGui().getInv());
          GUITracker.replacePlayersGUI(mp, gui);
          return;
        }
        else if(event.equalsIgnoreCase("Confirm")){
          String boosterType = events[1].toLowerCase();
          if(McBoosters.getInstance().getBoosterManager().isTypeMaxed(boosterType)){
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 1);
            return;
          }
          if(mp.hasBoosters(boosterType)){
            GUI newGUI = new ConfirmGUI(boosterType, new GUIBuilder("ConfirmGUI",
                    McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIRM_GUI), mp, false));
            currentGUI.setClearData(false);
            p.openInventory(newGUI.getGui().getInv());
            GUITracker.replacePlayersGUI(mp, newGUI);
            return;
          }
          else{
            p.sendMessage(Methods.color("&cYou do not have enough of that booster to activate one"));
            currentGUI.setClearData(true);
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 1);
            return;
          }
        }
      }
    }
  }
}
