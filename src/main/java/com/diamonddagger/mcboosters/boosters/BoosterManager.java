package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.api.events.BoosterEndEvent;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BoosterManager {

  private Map<String, List<Booster>> activeBoosters;
  private Map<String, Queue<Booster>> boostersInQueue;

  public BoosterManager(){

  }

  public void addBooster(Booster booster){

  }

  private void startUpdateTask(){
    new BukkitRunnable(){
      @Override
      public void run(){
        Calendar cal = Calendar.getInstance();
        for(String s : activeBoosters.keySet()){
          List<Booster> boosters = activeBoosters.get(s);
          List<Booster> clone = (List<Booster>) ((ArrayList) boosters).clone();
          for(Booster booster : boosters){
            if(cal.getTimeInMillis() >= booster.getEndTime()){
              OfflinePlayer owner = Bukkit.getOfflinePlayer(booster.getOwner());
              BoosterEndEvent boosterEndEvent = new BoosterEndEvent(owner, booster);
              Bukkit.getPluginManager().callEvent(boosterEndEvent);
              for(Player p : Bukkit.getOnlinePlayers()){
                p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.BoosterEnd")));
              }
              clone.remove(booster);
            }
          }
          boosters = clone;
        }
      }
    }.runTaskTimer(McBoosters.getInstance(), 20L, 30 * 20);
  }
}
