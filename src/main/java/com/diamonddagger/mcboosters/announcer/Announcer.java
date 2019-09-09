package com.diamonddagger.mcboosters.announcer;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.Booster;
import com.diamonddagger.mcboosters.discord.DiscordSRV;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announcer {

  private Map<String, List<String>> startMessages = new HashMap<>();
  private Map<String, List<String>> endMessages = new HashMap<>();

  private BukkitTask task;

  public void announceBoosterStart(Booster booster){
    OfflinePlayer boosterOwner = Bukkit.getOfflinePlayer(booster.getOwner());
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
              + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Started").replace("%Player%", boosterOwner.getName())
              .replace("%BoosterType%", booster.getBoosterInfo().getDisplayName())));
    }
    if(booster.getBoosterInfo().isDiscordSupportEnabled()){
      if(task != null){
        task.cancel();
      }
      if(startMessages.containsKey(booster.getBoosterInfo().getStartChannel())){
        startMessages.get(booster.getBoosterInfo().getStartChannel()).add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
      }
      else{
        List<String> messages = new ArrayList<>();
        messages.add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
        startMessages.put(booster.getBoosterInfo().getStartChannel(), messages);
      }
      task = new BukkitRunnable(){
        @Override
        public void run(){
          announce(startMessages, McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getBoolean("Configuration.Discord.RemovePings"));
          task = null;
          startMessages.clear();
        }
      }.runTaskLater(McBoosters.getInstance(), McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getInt("Configuration.Discord.MessageLimiterDuration") * 20);
    }
  }

  public void announceBoosterEnd(Booster booster){
    OfflinePlayer boosterOwner = Bukkit.getOfflinePlayer(booster.getOwner());
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
              + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Ended").replace("%Player%", boosterOwner.getName())
              .replace("%BoosterType%", booster.getBoosterInfo().getDisplayName())));
    }
    if(booster.getBoosterInfo().isDiscordSupportEnabled()){
      if(task != null){
        task.cancel();
      }
      if(endMessages.containsKey(booster.getBoosterInfo().getEndChannel())){
        endMessages.get(booster.getBoosterInfo().getEndChannel()).add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
      }
      else{
        List<String> messages = new ArrayList<>();
        messages.add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Ended")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
        endMessages.put(booster.getBoosterInfo().getEndChannel(), messages);
      }
      task = new BukkitRunnable(){
        @Override
        public void run(){
          announce(endMessages, McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getBoolean("Configuration.Discord.RemovePings"));
          endMessages.clear();
          task = null;
        }
      }.runTaskLater(McBoosters.getInstance(), McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getInt("Configuration.Discord.MessageLimiterDuration") * 20);
    }
  }

  private void announce(Map<String, List<String>> messages, boolean removePings){
    for(String s : messages.keySet()){
      boolean pinged = false;
      for(String message : messages.get(s)){
        if(removePings && (message.contains("@here") || message.contains("@everyone"))){
          if(!pinged){
            pinged = true;
          }
          else{
            message = message.replace("@here", "").replace("@everyone", "");
          }
        }
        DiscordSRV.sendMessage(s, message);
      }
    }
  }
}
