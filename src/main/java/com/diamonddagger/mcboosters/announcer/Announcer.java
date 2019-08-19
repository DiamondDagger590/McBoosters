package com.diamonddagger.mcboosters.announcer;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.Booster;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Announcer {

  public void announceBoosterStart(Booster booster){
    OfflinePlayer boosterOwner = Bukkit.getOfflinePlayer(booster.getOwner());
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
              + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Started").replace("%Player%", boosterOwner.getName())
      .replace("%BoosterType%", booster.getBoosterInfo().getDisplayName())));
    }
  }

  public void announceBoosterEnd(Booster booster){
    OfflinePlayer boosterOwner = Bukkit.getOfflinePlayer(booster.getOwner());
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
              + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Ended").replace("%Player%", boosterOwner.getName())
              .replace("%BoosterType%", booster.getBoosterInfo().getDisplayName())));    }
  }
}
