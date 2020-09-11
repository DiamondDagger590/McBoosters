package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.types.BoostType;
import com.diamonddagger.mcboosters.util.Methods;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class JobsEvents implements Listener {

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void money(JobsPaymentEvent e){
    String message = "&bJobs Money Boost- Money Before: &e" + e.getAmount();
    double boostedMoney = e.getAmount() * McBoosters.getInstance().getBoosterManager().getJobsMoneyBoost();
    double difference = boostedMoney - e.getAmount();
    e.setAmount(boostedMoney);
    
    message += " &bMoney After: &e" + e.getAmount();
    if(e.getPlayer().isOnline()){
      BoosterPlayer bp = McBoosters.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
      if(bp.isDebugMode()){
        bp.getPlayer().sendMessage(Methods.color(message));
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void exp(JobsExpGainEvent e){
    String message = "&bJobs Exp Boost For " + e.getJob().getName() + "- Exp Before: &e" + e.getExp();
    double boostedExp = e.getExp() * McBoosters.getInstance().getBoosterManager().getJobsExpBoost(e.getJob().getName());
    double difference = boostedExp - e.getExp();
    e.setExp(boostedExp);
    message += " &bExp After: &e" + e.getExp();
    
    McBoosters.getInstance().getBoosterManager().addBoostedExp(e.getPlayer().getUniqueId(), BoostType.JOBS, difference);
    if(e.getPlayer().isOnline()){
      BoosterPlayer bp = McBoosters.getInstance().getPlayerManager().getPlayer(e.getPlayer().getUniqueId());
      if(bp.isDebugMode()){
        bp.getPlayer().sendMessage(Methods.color(message));
      }
    }
  }

}
