package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.api.events.BoosterEndEvent;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import lombok.Getter;
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

public class BoosterManager{

	private Map<String, FileConfiguration> boosterFiles = new HashMap<>();
	@Getter private ArrayList<Booster> activeBoosters = new ArrayList<>();
	private Map<String, Queue<Booster>> boostersInQueue = new HashMap<>();

	public BoosterManager(){
		File boosterFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "boosters");
		if(!boosterFolder.exists()){
			boosterFolder.mkdir();
		}
		else{
			for(File f : boosterFolder.listFiles()){
				FileConfiguration boosterFile = YamlConfiguration.loadConfiguration(f);
				boosterFiles.put(boosterFile.getString("Booster.Name"), boosterFile);
			}
		}
		File f = new File(McBoosters.getInstance().getDataFolder(), File.separator + "boosterbackup.yml");
		boolean existed = f.exists();
		if(!existed){
			try{
				f.createNewFile();
			}
			catch(IOException e){
				Bukkit.getLogger().log(Level.WARNING, Methods.color("&cUnable to create save file for boosters"));
				e.printStackTrace();
			}
		}
		FileConfiguration backup = YamlConfiguration.loadConfiguration(f);
		if(backup.contains("Boosters")){
			for(String s : backup.getConfigurationSection("Boosters").getKeys(false)){
				String type = backup.getString("Boosters." + s + ".Type");
				Booster booster = BoosterFactory.getBooster(boosterFiles.get(type), backup, "Boosters." + s);
				activeBoosters.add(booster);
			}
		}
		startUpdateTask();
	}

	public boolean activateBooster(BoosterPlayer boosterPlayer, String boosterType){
		boolean passed = false;
		//verify conditions are right to activate the booster
		if(getMaxBoosterAmount() >= activeBoosters.size() &&
						boosterFiles.containsKey(boosterType) && boosterPlayer.getBoosterAmount(boosterType) > 1){
			Booster booster = BoosterFactory.getBooster(boosterFiles.get(boosterType), boosterPlayer.getUuid());
			activeBoosters.add(booster);
			backup();
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

	public int getMaxBoosterAmount(){
		return McBoosters.getInstance().getFileManager().getFile(FileManager.Files.CONFIG).getInt("Configuration.MaxAmountOfBoosters");
	}

	public boolean isABooster(String boosterType){
		return boosterFiles.keySet().stream().filter(s -> s.replace(" ", "").equalsIgnoreCase(boosterType)).count() == 1;
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
				ArrayList<Booster> clone = (ArrayList<Booster>) activeBoosters.clone();
				for(Booster booster : activeBoosters){
						if(cal.getTimeInMillis() >= booster.getEndTime()){
							OfflinePlayer owner = Bukkit.getOfflinePlayer(booster.getOwner());
							BoosterEndEvent boosterEndEvent = new BoosterEndEvent(owner, booster);
							Bukkit.getPluginManager().callEvent(boosterEndEvent);
							announceBoosterEnd(booster.getDisplayName());
							clone.remove(booster);
						}
				}
				activeBoosters = clone;
			}
		}.runTaskTimer(McBoosters.getInstance(), 20L, 30 * 20);
	}

	private void backup(){
		Calendar cal = Calendar.getInstance();
		File f = new File(McBoosters.getInstance().getDataFolder(), File.separator + "boosterbackup.yml");
		boolean existed = f.exists();
		if(!existed){
			try{
				f.createNewFile();
			}
			catch(IOException e){
				Bukkit.getLogger().log(Level.WARNING, Methods.color("&cUnable to create save file for boosters"));
				e.printStackTrace();
			}
		}
		FileConfiguration storage = YamlConfiguration.loadConfiguration(f);
		int i = 0;
		for(Booster booster : activeBoosters){
			if(existed){
				//delete the existing content
				for(String s : storage.getConfigurationSection("").getKeys(false)){
					storage.set(s, null);
				}
			}
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
