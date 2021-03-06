package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.util.Methods;
import com.diamonddagger.mcboosters.util.parser.Parser;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.economy.BufferedPayment;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import us.eunoians.mcrpg.api.exceptions.McRPGPlayerNotFoundException;
import us.eunoians.mcrpg.players.McRPGPlayer;
import us.eunoians.mcrpg.players.PlayerManager;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BaseBooster implements Booster {

  @Getter
  private BoosterInfo boosterInfo;

  private UUID boosterOwner;

  private long endTime;

  @Getter
  private Set<UUID> thankedPlayers;


  public BaseBooster(UUID boosterOwner, long endTime, BoosterInfo boosterInfo){
    this.boosterInfo = boosterInfo;
    this.boosterOwner = boosterOwner;
    this.endTime = endTime;
    this.thankedPlayers = new HashSet<>();
  }

  public BaseBooster(FileConfiguration backupFile, String key, BoosterInfo boosterInfo){
    this.boosterInfo = boosterInfo;
    this.boosterOwner = UUID.fromString(backupFile.getString(key + ".Owner"));
    Calendar cal = Calendar.getInstance();
    int remainingDuration = backupFile.getInt(key + ".RemainingDuration");
    cal.add(Calendar.SECOND, remainingDuration);
    this.endTime = remainingDuration > 0 ? cal.getTimeInMillis() : 0L;
    this.thankedPlayers = backupFile.getStringList(key + ".Thanked").stream().map(UUID::fromString).collect(Collectors.toSet());
  }

  public String getDisplayName(){
    return boosterInfo.getDisplayName();
  }

  @Override
  public void thank(Player thanker){
    OfflinePlayer owner = Bukkit.getOfflinePlayer(boosterOwner);
    thankedPlayers.add(thanker.getUniqueId());
    int vanillaExp = (int) boosterInfo.getThankReward().getVanillaExpReward().getValue();
    int mcrpgExp = 0;
    double jobsMoney = 0;
    if(!boosterInfo.getThankReward().getThankerCommands().isEmpty()){
      for(String command : boosterInfo.getThankReward().getThankerCommands()){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%Player%", thanker.getName()));
      }
    }
    if(McBoosters.getInstance().isMcrpgEnabled()){
      try{
        McRPGPlayer mp = PlayerManager.getPlayer(thanker.getUniqueId());
        Parser p = boosterInfo.getThankReward().getMcrpgExpReward();
        p.setVariable("power_level", mp.getPowerLevel());
        mcrpgExp = (int) p.getValue();
        mp.giveRedeemableExp(mcrpgExp);
        mp.saveData();
      } catch(McRPGPlayerNotFoundException e){
        return;
      }
    }
    if(McBoosters.getInstance().isJobsEnabled()){
      Parser p = boosterInfo.getThankReward().getJobsMoneyReward();
      int highestJob = 0;
      for(JobProgression progression : Jobs.getPlayerManager().getJobsPlayer(thanker).getJobProgression()){
        highestJob = Math.max(progression.getLevel(), highestJob);
      }
      p.setVariable("highest_job", highestJob);
      jobsMoney = p.getValue();
      if(jobsMoney > 0){
        Jobs.getEconomy().pay(new BufferedPayment(thanker, p.getValue(), 0, 0));
      }
    }
    thanker.giveExp(vanillaExp);
    thanker.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Thank")
    .replace("%Owner%", owner.getName()).replace("%BoosterType%", this.getDisplayName())));
    if(owner.isOnline()){
      Player onlineOwner = (Player) owner;
      onlineOwner.giveExp(vanillaExp);
      if(mcrpgExp > 0 && McBoosters.getInstance().isMcrpgEnabled()){
        try{
          McRPGPlayer mp = PlayerManager.getPlayer(onlineOwner.getUniqueId());
          mp.giveRedeemableExp(mcrpgExp);
          mp.saveData();
        } catch(McRPGPlayerNotFoundException e){
          e.printStackTrace();
        }
      }
      if(jobsMoney > 0 && McBoosters.getInstance().isJobsEnabled()){
        Parser p = boosterInfo.getThankReward().getJobsMoneyReward();
        int highestJob = 0;
        for(JobProgression progression : Jobs.getPlayerManager().getJobsPlayer(boosterOwner).getJobProgression()){
          highestJob = Math.max(progression.getLevel(), highestJob);
        }
        p.setVariable("highest_job", highestJob);
        jobsMoney = p.getValue();
        if(jobsMoney > 0){
          Jobs.getEconomy().pay(new BufferedPayment(Bukkit.getPlayer(boosterOwner), p.getValue(), 0, 0));
        }
      }
      if(!boosterInfo.getThankReward().getOwnerCommands().isEmpty()){
        for(String command : boosterInfo.getThankReward().getOwnerCommands()){
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%Player%", onlineOwner.getName()));
        }
      }
      onlineOwner.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Thanked")
      .replace("%Thanker%", thanker.getDisplayName())));
    }
    else{
      File playerStorageFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata");
      File playerFile = new File(playerStorageFolder, File.separator + owner.getUniqueId().toString() + ".yml");
      if(!playerFile.exists()){
        try{
          playerFile.createNewFile();
        } catch(IOException e){
          e.printStackTrace();
        }
      }
      //TODO
      FileConfiguration storage = YamlConfiguration.loadConfiguration(playerFile);
      if(vanillaExp > 0){
        if(storage.contains("CachedRewards.VanillaExp")){
          storage.set("CachedRewards.VanillaExp", vanillaExp + storage.getInt("CachedRewards.VanillaExp"));
        }
        else{
          storage.set("CachedRewards.VanillaExp", vanillaExp);
        }
      }
      if(mcrpgExp > 0){
        if(storage.contains("CachedRewards.McRPGExp")){
          storage.set("CachedRewards.McRPGExp", mcrpgExp + storage.getInt("CachedRewards.McRPGExp"));
        }
        else{
          storage.set("CachedRewards.McRPGExp", mcrpgExp);
        }
      }
      if(jobsMoney > 0){
        if(storage.contains("CachedRewards.JobsMoney")){
          storage.set("CachedRewards.JobsMoney", jobsMoney + storage.getInt("CachedRewards.JobsMoney"));
        }
        else{
          storage.set("CachedRewards.JobsMoney", jobsMoney);
        }
      }
      if(!boosterInfo.getThankReward().getOwnerCommands().isEmpty()){
        int startingIter = 0;
        if(storage.contains("CachedCommands")){
          for(String s : storage.getConfigurationSection("CachedCommands").getKeys(false)){
            startingIter++;
          }
        }
        for(String command : boosterInfo.getThankReward().getOwnerCommands()){
          startingIter++;
          String key = "CachedCommands." + Integer.toString(startingIter).replace("'", "");
          storage.set(key, command);
        }
      }
      try{
        storage.save(playerFile);
      } catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  @Override
  public boolean hasPlayerThanked(Player thanker){
    return thankedPlayers.contains(thanker.getUniqueId());
  }
  @Override
  public long getEndTime(){
    return endTime;
  }

  @Override
  public UUID getOwner(){
    return boosterOwner;
  }
}
