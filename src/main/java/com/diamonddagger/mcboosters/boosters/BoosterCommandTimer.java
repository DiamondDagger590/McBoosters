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
  private List<TimerCommand> commandsToRun;
  private List<TimerCommand> ownerCommandsToRun;
  private boolean useAmountParam;
  
  public BoosterCommandTimer(int delayInSeconds, List<TimerCommand> commandsToRun, List<TimerCommand> ownerCommandsToRun, String boosterName, boolean useAmountParam){
    this.commandsToRun = commandsToRun;
    this.ownerCommandsToRun = ownerCommandsToRun;
    this.delayInSeconds = delayInSeconds;
    this.boosterName = boosterName;
    this.useAmountParam = useAmountParam;
  }
  
  public void startRunnable(){
    if(currentTask != null){
      currentTask.cancel();
    }
    
    currentTask = new BukkitRunnable(){
      @Override
      public void run(){
  
        if(!useAmountParam){
          a: for(Player player : Bukkit.getOnlinePlayers()){
            b: for(TimerCommand command : commandsToRun){
              c: for(Booster booster : McBoosters.getInstance().getBoosterManager().getActiveBoosters(boosterName)){
                if(player.getUniqueId().equals(booster.getOwner()) && !ownerCommandsToRun.isEmpty()){
                  continue c;
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%Player%", player.getName()).replace("%player%", player.getName()));
              }
            }
          }
    
          for(Booster booster : McBoosters.getInstance().getBoosterManager().getActiveBoosters(boosterName)){
      
            Player player = Bukkit.getPlayer(booster.getOwner());
      
            if(player != null){
              for(TimerCommand command : ownerCommandsToRun){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%Player%", player.getName()).replace("%player%", player.getName()));
              }
            }
          }
        }
        
        else{
          for(Player player : Bukkit.getOnlinePlayers()){
  
            int amountOfOwnedBoosters = 0;
            int unownedBoosters = 0;
  
            for(Booster booster : McBoosters.getInstance().getBoosterManager().getActiveBoosters(boosterName)){
              if(booster.getOwner().equals(player.getUniqueId())){
                amountOfOwnedBoosters++;
              }
              else{
                unownedBoosters++;
              }
            }
            
            if(amountOfOwnedBoosters > 0){
              for(TimerCommand command : ownerCommandsToRun){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%Player%", player.getName())
                                                                    .replace("%player%", player.getName())
                                                                    .replace("%Amount%", Integer.toString(amountOfOwnedBoosters * command.getMultiplier()))
                                                                    .replace("%amount%", Integer.toString(amountOfOwnedBoosters * command.getMultiplier())));
  
              }
            }
            if(unownedBoosters > 0){
              for(TimerCommand command : ownerCommandsToRun){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.getCommand().replace("%Player%", player.getName())
                                                                    .replace("%player%", player.getName())
                                                                    .replace("%Amount%", Integer.toString(unownedBoosters * command.getMultiplier()))
                                                                    .replace("%amount%", Integer.toString(unownedBoosters * command.getMultiplier())));
      
              }
            }
          }
          
        }
      }
    }.runTaskTimer(McBoosters.getInstance(), delayInSeconds * 20, delayInSeconds * 20);
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
