package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakEvent implements Listener {

  @EventHandler
  public void breakEvent(BlockBreakEvent e){
    if(e.getExpToDrop() > 0){
      e.setExpToDrop((int) (e.getExpToDrop() * McBoosters.getInstance().getBoosterManager().getVanillaBoost(e.getBlock().getType().name())));
    }
  }
}
