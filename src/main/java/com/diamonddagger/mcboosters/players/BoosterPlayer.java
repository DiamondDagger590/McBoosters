package com.diamonddagger.mcboosters.players;

import com.diamonddagger.mcboosters.McBoosters;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoosterPlayer {

  @Getter
  private UUID uuid;

  private Map<String, Integer> boosterAmounts = new HashMap<>();

  File playerFile;
  FileConfiguration config;

  public BoosterPlayer(Player p){
    this.uuid = p.getUniqueId();
    playerFile = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
    boolean exists = playerFile.exists();
    if(!exists){
      try{
        playerFile.createNewFile();
      } catch(IOException e){
        e.printStackTrace();
      }
    }
    config = YamlConfiguration.loadConfiguration(playerFile);
    if(exists){
      if(config.contains("Boosters")){
        for(String s : config.getConfigurationSection("Boosters").getKeys(false)){
          boosterAmounts.put(s.toLowerCase(), config.getInt("Boosters." + s));
        }
      }
      //Deal with cached rewards
      if(config.contains("CachedRewards")){
        if(config.contains("CachedRewards.McRPGExp")){
          //TODO
        }
        if(config.contains("CachedRewards.VanillaExp")){
          p.giveExp(config.getInt("CachedRewards.VanillaExp"));
          //TODO message
          config.set("CahcedRewards.VanillaExp", null);
        }
        if(config.contains("CachedRewards.JobsMoney")){
          //TODO
        }
      }
      if(config.contains("CachedCommands")){
        for(String s : config.getConfigurationSection("CachedCommands").getKeys(false)){
          String key = "CachedCommands." + s + ".";
          Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString(key + "Command").replace("%Player%", p.getName()));
        }
        config.set("CachedCommands", null);
      }
      try{
        config.save(playerFile);
      } catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  public BoosterPlayer(OfflinePlayer p){
    this.uuid = p.getUniqueId();
    playerFile = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
    boolean exists = playerFile.exists();
    if(!exists){
      try{
        playerFile.createNewFile();
      } catch(IOException e){
        e.printStackTrace();
      }
    }
    config = YamlConfiguration.loadConfiguration(playerFile);
    if(exists){
      if(config.contains("Boosters")){
        for(String s : config.getConfigurationSection("Boosters").getKeys(false)){
          boosterAmounts.put(s.toLowerCase(), config.getInt("Boosters." + s));
        }
      }
    }
  }


  public Player getPlayer(){
    return Bukkit.getPlayer(uuid);
  }

  public boolean isOnline(){
    return Bukkit.getOfflinePlayer(uuid).isOnline();
  }

  public void giveBoosters(String boosterName, int amount){
    if(boosterAmounts.containsKey(boosterName)){
      boosterAmounts.replace(boosterName, boosterAmounts.get(boosterName) + amount);
    }
    else{
      boosterAmounts.put(boosterName, amount);
    }
    save();
  }

  public void setBoosters(String boosterName, int amount){
    if(boosterAmounts.containsKey(boosterName)){
      boosterAmounts.replace(boosterName, amount);
    }
    else{
      boosterAmounts.put(boosterName, amount);
    }
    save();
  }

  public boolean hasBoosters(String boosterName){
    return boosterAmounts.containsKey(boosterName);
  }

  public int getBoosterAmount(String boosterName){
    return boosterAmounts.getOrDefault(boosterName, 0);
  }

  //PRE: Validate that they have enough boosters for decrementing
  public void decrementBoosterAmount(String boosterName, int amount){
    int newAmount = boosterAmounts.get(boosterName) - amount;
    if(newAmount <= 0){
      boosterAmounts.remove(boosterName);
    }
    else{
      boosterAmounts.replace(boosterName, newAmount);
    }
  }

  public void save(){
    config.set("Boosters", null);
    for(String s : boosterAmounts.keySet()){
      config.set("Boosters." + s, boosterAmounts.get(s));
    }
    try{
      config.save(playerFile);
    } catch(IOException e){
      e.printStackTrace();
    }
  }
}