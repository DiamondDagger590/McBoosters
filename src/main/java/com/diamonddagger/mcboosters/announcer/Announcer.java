package com.diamonddagger.mcboosters.announcer;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.Booster;
import com.diamonddagger.mcboosters.discord.DiscordSRV;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Announcer {

  private List<AnnouncmentWrapper> startWrappers = new ArrayList<>();
  private List<AnnouncmentWrapper> endWrappers = new ArrayList<>();

  private BukkitTask announceTask;

  public void announceBoosterStart(Booster booster, String boosterType){
    OfflinePlayer boosterOwner = Bukkit.getOfflinePlayer(booster.getOwner());
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
              + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Started").replace("%Player%", boosterOwner.getName())
              .replace("%BoosterType%", booster.getBoosterInfo().getDisplayName())));
    }
    if(booster.getBoosterInfo().isDiscordSupportEnabled()){
      if(announceTask != null){
        announceTask.cancel();
        announceTask = null;
      }
      boolean found = false;
      for(AnnouncmentWrapper announcmentWrapper : startWrappers){
        if(announcmentWrapper.getBoosterType().equalsIgnoreCase(boosterType)){
          announcmentWrapper.setAmountAnnounced(announcmentWrapper.getAmountAnnounced() + 1);
          if(!announcmentWrapper.getPlayers().contains(booster.getOwner())){
            announcmentWrapper.getPlayers().add(booster.getOwner());
          }
          found = true;
        }
      }
      if(!found){
        startWrappers.add(new AnnouncmentWrapper(boosterType, booster.getBoosterInfo().getDisplayName(), booster.getBoosterInfo().getStartChannel(), McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started"), booster.getOwner()));
      }
     /* if(startMessages.containsKey(booster.getBoosterInfo().getStartChannel())){
        startMessages.get(booster.getBoosterInfo().getStartChannel()).add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
      }
      else{
        List<String> messages = new ArrayList<>();
        messages.add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
        startMessages.put(booster.getBoosterInfo().getStartChannel(), messages);
      }*/
      announceTask = new BukkitRunnable() {
        @Override
        public void run(){
          if(startWrappers.size() > 0){
            announce(startWrappers, McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getBoolean("Configuration.Discord.RemovePings"));
          }
          if(endWrappers.size() > 0){
            announce(endWrappers, McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getBoolean("Configuration.Discord.RemovePings"));
          }
          announceTask = null;
          startWrappers.clear();
          endWrappers.clear();
        }
      }.runTaskLater(McBoosters.getInstance(), McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getInt("Configuration.Discord.MessageLimiterDuration") * 20);
    }
  }

  public void announceBoosterEnd(Booster booster, String boosterType, boolean sendToChat){
    OfflinePlayer boosterOwner = Bukkit.getOfflinePlayer(booster.getOwner());
    if(sendToChat){
      for(Player p : Bukkit.getOnlinePlayers()){
        p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
                + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Ended").replace("%Player%", boosterOwner.getName())
                .replace("%BoosterType%", booster.getBoosterInfo().getDisplayName())));
      }
    }
    if(booster.getBoosterInfo().isDiscordSupportEnabled()){
      if(announceTask != null){
        announceTask.cancel();
      }

      boolean found = false;
      for(AnnouncmentWrapper announcmentWrapper : endWrappers){
        if(announcmentWrapper.getBoosterType().equalsIgnoreCase(boosterType)){
          announcmentWrapper.setAmountAnnounced(announcmentWrapper.getAmountAnnounced() + 1);
          if(!announcmentWrapper.getPlayers().contains(booster.getOwner())){
            announcmentWrapper.getPlayers().add(booster.getOwner());
          }
          found = true;
        }
      }
      if(!found){
        endWrappers.add(new AnnouncmentWrapper(boosterType, booster.getBoosterInfo().getDisplayName(), booster.getBoosterInfo().getEndChannel(), McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Ended"), booster.getOwner()));
      }
     /* if(startMessages.containsKey(booster.getBoosterInfo().getStartChannel())){
        startMessages.get(booster.getBoosterInfo().getStartChannel()).add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
      }
      else{
        List<String> messages = new ArrayList<>();
        messages.add(McBoosters.getInstance().getLangFile().getString("Messages.DiscordMessage.Started")
                .replace("%Player%", boosterOwner.getName()).replace("%BoosterType%", booster.getBoosterInfo().getDisplayName()));
        startMessages.put(booster.getBoosterInfo().getStartChannel(), messages);
      }*/
      announceTask = new BukkitRunnable() {
        @Override
        public void run(){
          if(startWrappers.size() > 0){
            announce(startWrappers, McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getBoolean("Configuration.Discord.RemovePings"));
          }
          if(endWrappers.size() > 0){
            announce(endWrappers, McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getBoolean("Configuration.Discord.RemovePings"));
          }
          announceTask = null;
          startWrappers.clear();
          endWrappers.clear();
        }
      }.runTaskLater(McBoosters.getInstance(), McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getInt("Configuration.Discord.MessageLimiterDuration") * 20);
    }
  }

  private void announce(List<AnnouncmentWrapper> wrappers, boolean removePings){
    boolean pinged = false;
    for(AnnouncmentWrapper wrapper : wrappers){
      StringBuilder players = new StringBuilder();
      if(wrapper.getPlayers().size() == 2){
        players.append(Bukkit.getOfflinePlayer(wrapper.getPlayers().get(0)).getName()).append(" and ").append(Bukkit.getOfflinePlayer(wrapper.getPlayers().get(1)).getName());
      }
      else{
        for(int i = 0; i < wrapper.getPlayers().size(); i++){
          if(i != 0){
            if(i == wrapper.getPlayers().size() - 1){
              players.append(" and ");
            }
            else{
              players.append(", ");
            }
          }
          if(i == 5){
            players.append(" etc.");
            break;
          }
          players.append(Bukkit.getOfflinePlayer(wrapper.getPlayers().get(i)).getName());
        }
      }
      String message = wrapper.getMessage().replace("%Amount%", Integer.toString(wrapper.getAmountAnnounced()))
              .replace("%Players%", players.toString()).replace("%BoosterType%", wrapper.getBoosterName() + "(s)");

      if(removePings && pinged && (message.contains("@here") || message.contains("@everyone"))) {
        message = message.replace("@here", "").replace("@everyone", "");
      }
      else{
        pinged = true;
      }
      DiscordSRV.sendMessage(wrapper.getChannel(), message);

    }
  }

  public void forceAnnounce(){
    if(announceTask != null){
      announceTask.cancel();
    }
    announce(startWrappers, true);
    announce(endWrappers, true);
  }

  private class AnnouncmentWrapper {

    @Getter
    private String boosterType;
    @Getter
    private String boosterName;
    @Getter
    private String message;
    @Getter
    private String channel;
    @Getter
    private List<UUID> players;
    @Getter
    @Setter
    private int amountAnnounced;

    AnnouncmentWrapper(String boosterType, String boosterName, String channel, String message){
      this.boosterType = boosterType;
      this.boosterName = boosterName;
      this.channel = channel;
      this.message = message;
      players = new ArrayList<>();
      amountAnnounced = 1;
    }

    AnnouncmentWrapper(String boosterType, String boosterName, String channel, String message, UUID player){
      this.boosterType = boosterType;
      this.boosterName = boosterName;
      this.channel = channel;
      this.message = message;
      players = new ArrayList<>();
      players.add(player);
      amountAnnounced = 1;
    }

    @Override
    public boolean equals(Object o){
      if(o instanceof AnnouncmentWrapper){
        AnnouncmentWrapper wrapper = (AnnouncmentWrapper) o;
        return wrapper.getBoosterType().equalsIgnoreCase(this.boosterType);
      }
      if(o instanceof String){
        return this.boosterType.equalsIgnoreCase((String) o);
      }
      return false;
    }
  }
}
