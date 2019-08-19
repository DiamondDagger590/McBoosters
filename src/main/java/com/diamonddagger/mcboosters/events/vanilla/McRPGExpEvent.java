package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import us.eunoians.mcrpg.api.events.mcrpg.McRPGPlayerExpGainEvent;

public class McRPGExpEvent implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void expGain(McRPGPlayerExpGainEvent e){
    BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
    double boost = boosterManager.getMcRPGBoost(e.getSkillGained().getName());
    e.setExpGained((int) (e.getExpGained() * boost));
  }
}
