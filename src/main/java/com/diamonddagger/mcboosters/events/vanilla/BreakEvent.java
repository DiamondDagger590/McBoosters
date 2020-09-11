package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.types.BoostType;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakEvent implements Listener {

  @EventHandler
  public void breakEvent(BlockBreakEvent e){
    if(e.getExpToDrop() > 0){
      String message = "&bVanilla Exp Boost For " + e.getBlock().getType().name() + "- Exp Before: &e" + e.getExpToDrop();
      
      double boostedExp = e.getExpToDrop() * McBoosters.getInstance().getBoosterManager().getVanillaBoost(e.getBlock().getType().name());
      double difference = boostedExp - e.getExpToDrop();
      
      McBoosters.getInstance().getBoosterManager().addBoostedExp(e.getPlayer().getUniqueId(), BoostType.VANILLA, difference);
      
      e.setExpToDrop((int) (boostedExp));
      message += " &bExp After: &e" + e.getExpToDrop();
      BoosterPlayer boosterPlayer = McBoosters.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
      if(boosterPlayer.isDebugMode()){
        e.getPlayer().sendMessage(Methods.color(message));
      }
    }
  }
}
