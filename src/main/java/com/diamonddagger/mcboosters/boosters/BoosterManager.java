package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.types.BoostType;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import com.diamonddagger.mcboosters.util.parser.Parser;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoosterManager {

  private HashMap<String, List<Booster>> activeBoosters = new HashMap<>();
  private Map<String, BoosterInfo> boosterInfoMap = new HashMap<>();
  private Map<UUID, BoosterRewardWrapper> boosterRewardWrappers = new HashMap<>();
  
  private BukkitTask validateTask;

  public BoosterManager(){
    setup(McBoosters.getInstance());
    startValidateTask(McBoosters.getInstance());
  }
  
  /**
   * Reloads the booster manager
   * @param plugin The main plugin instance
   */
  public void reload(McBoosters plugin){
    
    boosterInfoMap.clear();
    
    //Load in new booster config file
    FileConfiguration boosterConfig = plugin.getFileManager().getFile(FileManager.Files.BOOSTER_FILE);
    
    for(String boosterName : boosterConfig.getConfigurationSection("").getKeys(false)){
      boosterInfoMap.put(boosterName.toLowerCase(), new BoosterInfo(boosterConfig, boosterName));
    }
  }
  
  /**
   * Sets up the booster manager
   * @param plugin The main plugin instance
   */
  private void setup(McBoosters plugin){
    
    //Load in booster data
    FileConfiguration boosterConfig = plugin.getFileManager().getFile(FileManager.Files.BOOSTER_FILE);
    for(String boosterName : boosterConfig.getConfigurationSection("").getKeys(false)){
      boosterInfoMap.put(boosterName.toLowerCase(), new BoosterInfo(boosterConfig, boosterName));
    }
    
    //Backup boosters
    FileConfiguration boosterBackup = plugin.getFileManager().getFile(FileManager.Files.BOOSTER_BACKUP_FILE);
    if(boosterBackup.contains("Boosters")){
      for(String boosterID : boosterBackup.getConfigurationSection("Boosters").getKeys(false)){
        for(String i : boosterBackup.getConfigurationSection("Boosters." + boosterID).getKeys(false)){
          
          String key = "Boosters." + boosterID + "." + i + ".";
          String boosterName = boosterBackup.getString(key + "ID");
          if(boosterBackup.getInt(key + "RemainingDuration") <= 0){
            boosterBackup.set("Boosters." + boosterID, null);
            continue;
          }
          
          Booster booster = BoosterFactory.getBooster(boosterBackup, key, boosterInfoMap.get(boosterName));
          
         if(boosterInfoMap.containsKey(boosterID) && boosterInfoMap.get(boosterID).getBoosterCommandTimer() != null
          && !boosterInfoMap.get(boosterID).getBoosterCommandTimer().isRunning()){
           boosterInfoMap.get(boosterID).getBoosterCommandTimer().startRunnable();
         }
         
          if(activeBoosters.containsKey(boosterName)){
            activeBoosters.get(boosterName).add(booster);
          }
          else{
            ArrayList<Booster> list = new ArrayList<>();
            list.add(booster);
            activeBoosters.put(boosterName, list);
          }
        }
      }
      
      new BukkitRunnable(){
        @Override
        public void run(){
          plugin.getFileManager().saveFile(FileManager.Files.BOOSTER_BACKUP_FILE);
        }
      }.runTaskAsynchronously(McBoosters.getInstance());
    }
  }
  
  /**
   * Begins a validation task for booster updating
   * @param plugin
   */
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
    for(String boosterName : clone.keySet()){
      //remove if empty
      if(clone.get(boosterName).isEmpty()){
        activeBoosters.remove(boosterName);
      }
      else{
        List<Booster> newList = (List<Booster>) ((ArrayList) clone.get(boosterName)).clone();
        List<Integer> toRemove = new ArrayList<>();
  
        Calendar cal = Calendar.getInstance();
        //iterate through all the boosters
        for(int i = 0; i < newList.size(); i++){
          
          Booster booster = activeBoosters.get(boosterName).get(i);
          
          //cancel boosters
          if(booster.getEndTime() <= cal.getTimeInMillis()){
            toRemove.add(i);
            plugin.getAnnouncer().announceBoosterEnd(booster, boosterName, true);
          }
        }
        
        for(int i = toRemove.size() - 1; i >= 0; i--){
          int x = toRemove.get(i);
          activeBoosters.get(boosterName).remove(x);
        }
        
        if(activeBoosters.get(boosterName).isEmpty()){
          if(boosterInfoMap.get(boosterName).getBoosterCommandTimer() != null){
            boosterInfoMap.get(boosterName).getBoosterCommandTimer().endRunnable();
          }
          activeBoosters.remove(boosterName);
        }
      }
      
      if(activeBoosters.containsKey(boosterName)){
        int i = 1;
        for(Booster booster : activeBoosters.get(boosterName)){
          String key = "Boosters." + boosterName + "." + i + ".";
          backup.set(key + "ID", boosterName);
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
  
  /**
   * Gets all booster types available
   * @return A {@link Set} containing all booster types in lowercase
   */
  public Set<String> getAllBoosterTypes(){
    return boosterInfoMap.keySet();
  }
  
  /**
   * Gets all booster types currently active
   * @return A {@link Set} containing all booster types in lowercase that are currently active
   */
  public Set<String> getAllActiveBoosterTypes(){
    return activeBoosters.keySet();
  }
  
  /**
   * Gets the time until the next booster ends for the provided type.
   * @param boosterType The name of the booster to check for
   * @return -1 if there isn't an active booster for the type, otherwise the shortest remaining duration
   */
  public long getNextEndingBooster(String boosterType){
    long soonestEndTime = -1;
    if(activeBoosters.containsKey(boosterType)){
      for(Booster booster : activeBoosters.get(boosterType)){
        if(soonestEndTime == -1){
          soonestEndTime = booster.getEndTime();
        }
        if(booster.getEndTime() < soonestEndTime){
          soonestEndTime = booster.getEndTime();
        }
      }
    }
    return soonestEndTime;
  }
  
  /**
   * Checks to see if the string provided is a valid booster type
   * @param type The {@link String} to be tested
   * @return True if the string is a valid booster type
   */
  public boolean isBooster(String type){
    return boosterInfoMap.containsKey(type.toLowerCase());
  }
  
  /**
   * Cancels all boosters for the provided booster type
   * @param boosterType The {@link String} boosterType to cancel
   */
  public void cancelBooster(String boosterType){
    
    for(Booster booster : activeBoosters.get(boosterType)){
      McBoosters.getInstance().getAnnouncer().announceBoosterEnd(booster, boosterType, false);
    }
  
    if(boosterInfoMap.get(boosterType).getBoosterCommandTimer() != null){
      boosterInfoMap.get(boosterType).getBoosterCommandTimer().endRunnable();
    }
    
    activeBoosters.remove(boosterType);
    
    for(Player player : Bukkit.getOnlinePlayers()){
      player.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.Cancelled")
      .replace("%BoosterType%", boosterType)));
    }
  }
  
  /**
   * Thanks all active boosters for the {@link Player}
   * @param player The {@link Player} to thank all active boosters for
   */
  public void thankAllBoosters(Player player){
    for(String boosterType : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(boosterType)){
        
        //Check to make sure they aren't the owner or that they haven't already thanked
        if(!player.getUniqueId().equals(booster.getOwner()) && !booster.hasPlayerThanked(player)){
          booster.thank(player);
        }
      }
    }
  }
  
  /**
   * Makes all online players thank the {@link Booster}
   * @param booster The {@link Booster} to have all online players thank
   */
  public void thankAllBoosters(Booster booster){
    
    for(Player player : Bukkit.getOnlinePlayers()){
      
      //Ensure the player isn't the booster owner and that they haven't thanked.
      if(!player.getUniqueId().equals(booster.getOwner()) && !booster.hasPlayerThanked(player)){
        booster.thank(player);
      }
    }
  }
  
  /**
   * Gets the amount of active boosters for the provided type
   * @param boosterType The {@link String} boosterType
   * @return The amount of active boosters for the provided type
   */
  public int getAmountActive(String boosterType){
    return activeBoosters.containsKey(boosterType) ? activeBoosters.get(boosterType).size() : 0;
  }

  //PRE: validate that the user has enough boosters to activate one
  public void activateBooster(BoosterPlayer boosterPlayer, String boosterName){
    
    Booster booster = BoosterFactory.getBooster(boosterPlayer.getUuid(), boosterInfoMap.get(boosterName));
    
    if(activeBoosters.containsKey(boosterName)){
      activeBoosters.get(boosterName).add(booster);
    }
    else{
      ArrayList<Booster> list = new ArrayList<>();
      list.add(booster);
      activeBoosters.put(boosterName, list);
    }
  
    if(boosterInfoMap.get(boosterName).getBoosterCommandTimer() != null && !boosterInfoMap.get(boosterName).getBoosterCommandTimer().isRunning()){
      boosterInfoMap.get(boosterName).getBoosterCommandTimer().startRunnable();
    }
    
    McBoosters.getInstance().getAnnouncer().announceBoosterStart(booster, boosterName);
    
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
  
  /**
   * Adds an amount of boosted exp to the player for the specified booster id
   *
   * @param uuid The {@link UUID} of the player getting the boosted exp
   * @param boostType The {@link BoostType} that gave the exp boost
   * @param boostedExp The amount of extra exp the player got
   */
  public void addBoostedExp(UUID uuid, BoostType boostType, double boostedExp){
      if(boosterRewardWrappers.containsKey(uuid)){
        boosterRewardWrappers.get(uuid).addBoostFromType(boostType, boostedExp);
      }
      else{
        BoosterRewardWrapper boosterRewardWrapper = new BoosterRewardWrapper(uuid);
        boosterRewardWrapper.setBoostFromType(boostType, boostedExp);
        boosterRewardWrappers.put(uuid, boosterRewardWrapper);
      }
  }

  public double getVanillaBoost(String type){
    double currentBoost = 0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        if(currentBoost == 0){
          currentBoost = booster.getBoosterInfo().getBoostWrapper().getVanillaBoost(type);
          continue;
        }
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getVanillaBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost == 0 ? 1 : currentBoost;
  }

  public double getMcRPGBoost(String type){
    double currentBoost = 0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        if(currentBoost == 0){
          currentBoost = booster.getBoosterInfo().getBoostWrapper().getMcRPGBoost(type);
          continue;
        }
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getMcRPGBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost == 0 ? 1 : currentBoost;
  }

  public double getMcMMOBoost(String type){
    double currentBoost = 0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        if(currentBoost == 0){
          currentBoost = booster.getBoosterInfo().getBoostWrapper().getMcMMOBoost(type);
          continue;
        }
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getMcMMOBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost == 0 ? 1 : currentBoost;
  }

  public double getJobsMoneyBoost(){
    double currentBoost = 0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        if(currentBoost == 0){
          currentBoost = booster.getBoosterInfo().getBoostWrapper().getJobsMoneyBoost();
          continue;
        }
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getJobsMoneyBoost());
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost == 0 ? 1 : currentBoost;
  }

  public double getJobsExpBoost(String type){
    double currentBoost = 0;
    Parser stackEquation = new Parser(McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getString("Configuration.StackedBoosterBoost"));
    for(String s : activeBoosters.keySet()){
      for(Booster booster : activeBoosters.get(s)){
        if(currentBoost == 0){
          currentBoost = booster.getBoosterInfo().getBoostWrapper().getJobsExpBoost(type);
          continue;
        }
        stackEquation.setVariable("boost", currentBoost);
        stackEquation.setVariable("extra_boost", booster.getBoosterInfo().getBoostWrapper().getJobsExpBoost(type));
        currentBoost = stackEquation.getValue();
      }
    }
    return currentBoost == 0 ? 1 : currentBoost;
  }

  public BoosterInfo getBoosterInfo(String boosterName){
    return boosterInfoMap.getOrDefault(boosterName, null);
  }

}