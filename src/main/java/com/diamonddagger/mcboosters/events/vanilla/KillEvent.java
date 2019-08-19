package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEvent implements Listener {

  @EventHandler(priority = EventPriority.NORMAL)
  public void killEvent(EntityDeathEvent e){
    BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
    e.setDroppedExp((int) (e.getDroppedExp() * boosterManager.getVanillaBoost(e.getEntityType().name())));
  }
}
