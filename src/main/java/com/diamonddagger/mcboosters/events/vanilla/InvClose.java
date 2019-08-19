package com.diamonddagger.mcboosters.events.vanilla;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import us.eunoians.mcrpg.gui.GUITracker;

public class InvClose implements Listener {

  @EventHandler
  public void close(InventoryCloseEvent e){
    Player p = (Player) e.getPlayer();
    if(GUITracker.isPlayerTracked(p)){
      if(GUITracker.getPlayersGUI(p).isClearData()){
        GUITracker.stopTrackingPlayer(p);
      }
    }
  }
}
