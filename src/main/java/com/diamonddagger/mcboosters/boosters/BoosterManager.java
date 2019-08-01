package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.api.events.BoosterEndEvent;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BoosterManager {

  private Map<String, FileConfiguration> boosterFiles;
  private Map<String, List<Booster>> activeBoosters;
  private Map<String, Queue<Booster>> boostersInQueue;

  public BoosterManager(){

  }

  public boolean activateBooster(BoosterPlayer boosterPlayer, String boosterType){
    boolean passed = false;
    //verify conditions are right to activate the booster
    if(getMaxBoosterAmount(boosterType) >= activeBoosters.get(boosterType).size() &&
            boosterFiles.containsKey(boosterType) && boosterPlayer.getBoosterAmount(boosterType) > 1){
      Booster booster = BoosterFactory.getBooster(boosterFiles.get(boosterType), boosterPlayer.getUuid());
      activeBoosters.get(boosterType).add(booster);
      backup(boosterType);
      announceBoosterStart(boosterType);
      passed = true;
    }
    return passed;
  }

  public Set<String> getAllBoosters(){
  	return boosterFiles.keySet();
  }
  public FileConfiguration getBoosterFile(String booster){
    return boosterFiles.get(booster);
  }

  public int getMaxBoosterAmount(String boosterType){
    return boosterFiles.getOrDefault(boosterType, null) != null ? boosterFiles.get(boosterType).getInt("Booster.MaxNumberOfBoosters", 0) : 0;
  }

  public void announceBoosterStart(String boosterType){
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.BoosterStart")
              .replace("%BoosterType%", boosterType)));
    }
    McBoosters.getInstance().getDiscordManager().announceBoosterStart(boosterType);
  }

  public void announceBoosterEnd(String boosterType){
    for(Player p : Bukkit.getOnlinePlayers()){
      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.BoosterEnd")
              .replace("%BoosterType%", boosterType)));
    }
    McBoosters.getInstance().getDiscordManager().announceBoosterEnd(boosterType);
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

  private void backup(){
    File boosterFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "booster_backup");
    Calendar cal = Calendar.getInstance();
    if(!boosterFolder.exists()){
      boosterFolder.mkdir();
    }
    for(String boosterType : activeBoosters.keySet()){
	    File f = new File(boosterFolder, File.separator + boosterType.replace(".", "")
					    .replace("-", "").replace(" ", "").replace("/", "").replace(File.separator, ""));
	    boolean existed = f.exists();
	    if(!existed){
		    try{
			    f.createNewFile();
		    }
		    catch(IOException e){
			    Bukkit.getLogger().log(Level.WARNING, Methods.color("&cUnable to create save file for Booster type:" + boosterType));
			    e.printStackTrace();
			    continue;
		    }
	    }
	    FileConfiguration storage = YamlConfiguration.loadConfiguration(f);
	    if(existed){
		    //delete the existing content
		    for(String s : storage.getConfigurationSection("").getKeys(false)){
			    storage.set(s, null);
		    }
	    }
	    List<Booster> boosters = activeBoosters.get(boosterType);
	    int i = 0;
	    for(Booster booster : boosters){
	    	i++;
	    	List<String> thankedPlayers = booster.getThankedPlayers().stream().map(UUID::toString).collect(Collectors.toList());
	    	storage.set("Booster" + i + ".ThankedPlayers", thankedPlayers);
	    	storage.set("Booster" + i + ".Owner", booster.getOwner().toString());
	    	int timeLeft = (int) ((booster.getEndTime() - cal.getTimeInMillis())/1000);
	    	storage.set("Booster" + i + ".TimeLeft", timeLeft);
	    }
	    try{
		    storage.save(f);
	    }
	    catch(IOException e){
		    e.printStackTrace();
	    }
    }
  }

	private void backup(String boosterType){
		File boosterFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "booster_backup");
		Calendar cal = Calendar.getInstance();
		if(!boosterFolder.exists()){
			boosterFolder.mkdir();
		}
		File f = new File(boosterFolder, File.separator + boosterType.replace(".", "")
						.replace("-", "").replace(" ", "").replace("/", "").replace(File.separator, ""));
		boolean existed = f.exists();
		if(!existed){
			try{
				f.createNewFile();
			}
			catch(IOException e){
				Bukkit.getLogger().log(Level.WARNING, Methods.color("&cUnable to create save file for Booster type:" + boosterType));
				e.printStackTrace();
				return;
			}
		}
		FileConfiguration storage = YamlConfiguration.loadConfiguration(f);
		if(existed){
			//delete the existing content
			for(String s : storage.getConfigurationSection("").getKeys(false)){
				storage.set(s, null);
			}
		}
		List<Booster> boosters = activeBoosters.get(boosterType);
		int i = 0;
		for(Booster booster : boosters){
			i++;
			List<String> thankedPlayers = booster.getThankedPlayers().stream().map(UUID::toString).collect(Collectors.toList());
			storage.set("Booster" + i + ".ThankedPlayers", thankedPlayers);
			storage.set("Booster" + i + ".Owner", booster.getOwner().toString());
			int timeLeft = (int) ((booster.getEndTime() - cal.getTimeInMillis()) / 1000);
			storage.set("Booster" + i + ".TimeLeft", timeLeft);
		}
		try{
			storage.save(f);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}


	private void initBoostersFromStorage(){
    File boosterFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "booster_backup");
    if(boosterFolder.exists()){
      File[] subFiles = boosterFolder.listFiles();
      if(subFiles != null){
        for(File subFile : subFiles){
          FileConfiguration boosterData = YamlConfiguration.loadConfiguration(subFile);
        }
      }
    }
  }
}
