package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.gamingmesh.jobs.api.JobsExpGainEvent;
import com.gamingmesh.jobs.api.JobsPaymentEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class JobsEvents implements Listener {

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void money(JobsPaymentEvent e){
    e.setAmount(e.getAmount() * McBoosters.getInstance().getBoosterManager().getJobsMoneyBoost());
  }

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void exp(JobsExpGainEvent e){
    e.setExp(e.getExp() * McBoosters.getInstance().getBoosterManager().getJobsExpBoost(e.getJob().getName()));
  }

}
