package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.types.BoostType;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import us.eunoians.mcrpg.api.events.mcrpg.McRPGPlayerExpGainEvent;

public class McRPGExpEvent implements Listener{
  
  @EventHandler(priority = EventPriority.LOWEST)
  public void expGain(McRPGPlayerExpGainEvent e){
    BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
    BoosterPlayer bp = McBoosters.getInstance().getPlayerManager().getPlayer(e.getMcRPGPlayer().getUuid());
    String message = "&bMcRPG Exp Boost For " + e.getSkillGained().getName() + "- Exp Before: &e" + e.getExpGained();
    
    double boost = boosterManager.getMcRPGBoost(e.getSkillGained().getName());
    double boostedExp = e.getExpGained() * boost;
    double difference = boostedExp - e.getExpGained();
    
    McBoosters.getInstance().getBoosterManager().addBoostedExp(e.getMcRPGPlayer().getUuid(), BoostType.MCRPG, difference);
    
    e.setExpGained((int) (boostedExp));
    message += " &bExp After: &e" + e.getExpGained();
    if(bp.isDebugMode()){
      bp.getPlayer().sendMessage(Methods.color(message));
    }
  }
}
