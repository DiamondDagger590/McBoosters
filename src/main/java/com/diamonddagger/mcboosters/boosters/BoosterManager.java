package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import com.diamonddagger.mcboosters.util.parser.Parser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class BoosterManager {

  private HashMap<String, List<Booster>> activeBoosters = new HashMap<>();
  private Map<String, BoosterInfo> boosterInfoMap = new HashMap<>();
  private BukkitTask validateTask;

  public BoosterManager(){
    setup(McBoosters.getInstance());
    startValidateTask(McBoosters.getInstance());
  }

  private void setup(McBoosters plugin){
    //Load in booster data
    FileConfiguration boosterConfig = plugin.getFileManager().getFile(FileManager.Files.BOOSTER_FILE);
    for(String s : boosterConfig.getConfigurationSection("").getKeys(false)){
      boosterInfoMap.put(s.toLowerCase(), new BoosterInfo(boosterConfig, s));
    }
    //Backup boosters
    FileConfiguration boosterBackup = plugin.getFileManager().getFile(FileManager.Files.BOOSTER_BACKUP_FILE);
    if(boosterBackup.contains("Boosters")){
      for(String s : boosterBackup.getConfigurationSection("Boosters").getKeys(false)){
        String key = "Boosters." + s.toLowerCase() + ".";
        String boosterName = boosterBackup.getString(key + "ID");
        if(boosterBackup.getInt(key + "RemainingDuration") <= 0){
          boosterBackup.set("Boosters." + s, null);
          continue;
        }
        Booster booster = BoosterFactory.getBooster(boosterConfig, boosterBackup, key, boosterName);
        if(activeBoosters.containsKey(boosterName)){
          activeBoosters.get(boosterName).add(booster);
        }
        else{
          ArrayList<Booster> list = new ArrayList<>();
          list.add(booster);
          activeBoosters.put(boosterName, list);
        }
      }
      plugin.getFileManager().saveFile(FileManager.Files.BOOSTER_BACKUP_FILE);
    }
  }

  private void startValidateTask(McBoosters plugin){
    if(validateTask != null){
      validateTask.cancel();
    }
    else{
      validateTask = new BukkitRunnable() {
        @Override
        public void run(){
          backup();
        }
      }.runTaskTimer(plugin, 180, 1 * 60 * 20);
    }
  }

  public void backup(){
    McBoosters plugin = McBoosters.getInstance();
    Map<String, List<Booster>> clone = (HashMap<String, List<Booster>>) activeBoosters.clone();
    FileConfiguration backup = plugin.getFileManager().getFile(FileManager.Files.BOOSTER_BACKUP_FILE);
    backup.set("Boosters", null);
    //iter through all boosterNames
    for(String s : clone.keySet()){
      //remove if empty
      if(clone.get(s).isEmpty()){
        activeBoosters.remove(s);
      }
      else{
        List<Booster> newList = (List<Booster>) ((ArrayList) clone.get(s)).clone();
        List<Integer> toRemove = new ArrayList<>();
        if(newList == activeBoosters.get(s)){
          Bukkit.broadcastMessage("why tf");
        }
        //iterate through all the boosters
        for(int i = 0; i < newList.size(); i++){
          Booster booster = activeBoosters.get(s).get(i);
          //cancel boosters
          Calendar cal = Calendar.getInstance();
          if(booster.getEndTime() <= cal.getTimeInMillis()){
            toRemove.add(i);
            plugin.getAnnouncer().announceBoosterEnd(booster);
          }
        }
        for(int i = toRemove.size() - 1; i >= 0; i--){
          activeBoosters.get(s).remove(i);
        }
        if(newList.isEmpty()){
          activeBoosters.remove(s);
        }
      }
      if(activeBoosters.containsKey(s)){
        int i = 1;
        for(Booster booster : activeBoosters.get(s)){
          String key = "Boosters." + i + ".";
          backup.set(key + "ID", s);
          backup.set(key + "Owner", booster.getOwner().toString());
          backup.set(key + "Thanked", booster.getThankedPlayers().stream().map(UUID::toString).collect(Collectors.toList()));
          Calendar cal = Calendar.getInstance();
          long remainingMillis = booster.getEndTime() - cal.getTimeInMillis();
          backup.set(key + "RemainingDuration", remainingMillis / 1000);
          i++;
        }
      }
    }
    plugin.getFileManager().saveFile(FileManager.Files.BOOSTER_BACKUP_FILE);
  }

  public Set<String> getAllBoosterTypes(){
    return boosterInfoMap.keySet();
  }

  public Set<String> getAllActiveBoosterTypes(){
    return activeBoosters.keySet();
  }

  public long getNextEndingBooster(String boosterType){
    long soonestEndTime = 0;
    if(activeBoosters.containsKey(boosterType)){
      for(Booster booster : activeBoosters.get(boosterType)){
        if(soonestEndTime == 0){
          soonestEndTime = booster.getEndTime();
        }
        if(booster.getEndTime() < soonestEndTime){
          soonestEndTime = booster.getEndTime();
        }
      }
    }
    return soonestEndTime;
  }

  public boolean isBooster(String type){
    return boosterInfoMap.containsKey(type.toLowerCase());
  }

  public void cancelBooster(String boosterType){
    activeBoosters.remove(boosterType);
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Cancelled")
      .replace("%BoosterType%", boosterType)));
    }
  }

  public void thankAllBoosters(Player p){
    for(String s : activeBoosters.keySet()){
      Bukkit.broadcastMessage("1");
      for(Booster booster : activeBoosters.get(s)){
        Bukkit.broadcastMessage("2");
        if(!p.getUniqueId().equals(booster.getOwner()) && !booster.hasPlayerThanked(p)){
          Bukkit.broadcastMessage("3");
          booster.thank(p);
        }
      }
    }
  }

  public void thankAllBoosters(Booster booster){
    for(Player p : Bukkit.getOnlinePlayers()){
      if(!p.getUniqueId().equals(booster.getOwner()) && !booster.hasPlayerThanked(p)){
        booster.thank(p);
      }
    }
  }

  //PRE: validate that the user has enough boosters to activate one
  public void activateBooster(BoosterPlayer boosterPlayer, String boosterName){
    Booster booster = BoosterFactory.getBooster(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.BOOSTER_FILE), boosterPlayer.getUuid(), boosterName);
    if(activeBoosters.containsKey(boosterName)){
      activeBoosters.get(boosterName).add(booster);
    }
    else{
      ArrayList<Booster> list = new ArrayList<>();
      list.add(booster);
      activeBoosters.put(boosterName, list);
    }
    McBoosters.getInstance().getAnnouncer().announceBoosterStart(booster);
    backup();
    new BukkitRunnable() {
      @Override
      public void run(){
        thankAllBoosters(booster);
      }
    }.runTaskLater(McBoosters.getInstance(), 3 * 20);
  }

  public boolean isTypeMaxed(String boosterName){
    return activeBoosters.containsKey(boosterName.toLowerCase()) &&
            activeBoosters.get(boosterName.toLowerCase()).size() >= (boosterInfoMap.containsKey(boosterName.toLowerCase())
                    ? boosterInfoMap.get(boosterName.toLowerCase()).getMaxAmount() : 0);
  }

  public List<Booster> getActiveBoosters(String boosterName){
    return activeBoosters.containsKey(boosterName) ? activeBoosters.get(boosterName) : new ArrayList<>();
  }

  public double getVanillaBoost(String type){
    double currentBoost = 1.0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getVanillaBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost;
  }

  public double getMcRPGBoost(String type){
    double currentBoost = 1.0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getMcRPGBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost;
  }

  public double getMcMMOBoost(String type){
    double currentBoost = 1.0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getMcMMOBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost;
  }

  public double getJobsMoneyBoost(){
    double currentBoost = 1.0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getJobsMoneyBoost());
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost;
  }

  public double getJobsExpBoost(String type){
    double currentBoost = 1.0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getJobsExpBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost;
  }

  public BoosterInfo getBoosterInfo(String boosterName){
    return boosterInfoMap.getOrDefault(boosterName, null);
  }

}