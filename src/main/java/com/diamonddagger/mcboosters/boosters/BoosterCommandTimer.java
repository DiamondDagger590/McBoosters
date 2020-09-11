package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class BoosterCommandTimer{
  
  private BukkitTask currentTask;
  private BukkitRunnable runnable;
  private int delayInSeconds;
  private String boosterName;
  private List<String> commandsToRun;
  
  public BoosterCommandTimer(int delayInSeconds, List<String> commandsToRun, String boosterName){
    this.commandsToRun = commandsToRun;
    this.delayInSeconds = delayInSeconds;
    this.boosterName = boosterName;
    
    this.runnable = new BukkitRunnable(){
      @Override
      public void run(){
        for(Player player : Bukkit.getOnlinePlayers()){
          for(String command : commandsToRun){
            for(int i = 0; i < McBoosters.getInstance().getBoosterManager().getAmountActive(boosterName); i++){
              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%Player%", player.getName()).replace("%player%", player.getName()));
            }
          }
        }
      }
    };
  }
  
  public void startRunnable(){
    if(currentTask != null){
      currentTask.cancel();
    }
    
    currentTask = runnable.runTaskTimer(McBoosters.getInstance(), delayInSeconds * 20, delayInSeconds * 20);
  }
  
  public void endRunnable(){
    if(currentTask != null){
      currentTask.cancel();
      currentTask = null;
    }
  }
  
  public boolean isRunning(){
    return currentTask != null;
  }
}
