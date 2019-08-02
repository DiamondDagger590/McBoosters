package com.diamonddagger.mcboosters.players;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.util.Methods;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.eunoians.mcrpg.api.exceptions.McRPGPlayerNotFoundException;
import us.eunoians.mcrpg.players.McRPGPlayer;
import us.eunoians.mcrpg.players.PlayerManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class BoosterPlayer {

  @Getter
  private Player player;

  @Getter
  private UUID uuid;

  private HashMap<String, Integer> boosters = new HashMap<>();

  private File playerFile;

  @Getter
  private FileConfiguration storage;

  public BoosterPlayer(Player p){
    this.player = p;
    this.uuid = p.getUniqueId();
	  File playerStorageFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata");
	  if(!playerStorageFolder.exists()){
		  playerStorageFolder.mkdir();
	  }
	  playerFile = new File(playerStorageFolder, File.separator + uuid.toString() + ".yml");
	  if(!playerFile.exists()){
		  try{
			  playerFile.createNewFile();
		  }
		  catch(IOException e){
			  e.printStackTrace();
		  }
	  }
	  //TODO
	  storage = YamlConfiguration.loadConfiguration(playerFile);
	  if(storage.contains("VanillaExp")){
	  	int vanillaExp = storage.getInt("VanillaExp");
	  	if(vanillaExp > 0){
	  		p.giveExp(vanillaExp);
	  		p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&While you were offline, you were given %Amount% vanilla exp").replace("%Amount%", Integer.toString(vanillaExp)));
		  }
	  	storage.set("VanillaExp", null);
	  }
	  if(storage.contains("McRPGExp")){
		  int mcRPGExp = storage.getInt("McRPGExp");
		  if(mcRPGExp > 0){
			  if(McBoosters.getInstance().isMcrpgEnabled()){
				  try{
					  McRPGPlayer mp = PlayerManager.getPlayer(uuid);
					  mp.giveRedeemableExp(mcRPGExp);
					  p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&While you were offline, you were given %Amount% McRPG exp").replace("%Amount%", Integer.toString(mcRPGExp)));
					  storage.set("McRPGExp", null);
				  }
				  catch(McRPGPlayerNotFoundException e){
					  new BukkitRunnable(){
						  @Override
						  public void run(){
							  try{
								  McRPGPlayer mp = PlayerManager.getPlayer(uuid);
								  mp.giveRedeemableExp(mcRPGExp);
								  if(p.isOnline()){
									  p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&While you were offline, you were given %Amount% McRPG exp").replace("%Amount%", Integer.toString(mcRPGExp)));
								  }
								  storage.set("McRPGExp", null);
							  }
							  catch(McRPGPlayerNotFoundException ex){ }
						  }
					  }.runTaskLater(McBoosters.getInstance(), 30 * 20);
				  }
			  }
		  }
		  storage.set("McRPGExp", null);
	  }
	  loadBoosters();
	  save();
  }

	public BoosterPlayer(UUID p){
		this.uuid = p;
		File playerStorageFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata");
		if(!playerStorageFolder.exists()){
			playerStorageFolder.mkdir();
		}
		playerFile = new File(playerStorageFolder, File.separator + uuid.toString() + ".yml");
		if(!playerFile.exists()){
			try{
				playerFile.createNewFile();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		storage = YamlConfiguration.loadConfiguration(playerFile);
		loadBoosters();
		save();
	}

  public boolean doesPlayerHaveBooster(String booster){
  	return boosters.containsKey(booster);
  }

  public boolean doesPlayerHaveAnyBoosters(){
  	return boosters.values().stream().anyMatch(i -> i > 0);
  }

  public int getBoosterAmount(String booster){
  	return boosters.getOrDefault(booster, 0);
  }

  public void giveBoosters(String boosterType, int amount){
  	setBoosterAmount(boosterType, boosters.containsKey(boosterType) ? boosters.get(boosterType) + amount : amount);
  }
  public void setBoosterAmount(String boosterType, int amount){
  	boosters.put(boosterType, amount);
  }

  public void decrementBoosterAmount(String boosterType){
  	if(boosters.containsKey(boosterType)){
  		int amount = boosters.get(boosterType);
  		if(amount - 1 <= 0){
  			boosters.remove(boosterType);
  		}
  		else{
  			boosters.replace(boosterType, amount - 1);
		  }
	  }
  }

	private void loadBoosters(){
  	if(storage.contains("Boosters")){
  		for(String s : storage.getConfigurationSection("Boosters").getKeys(false)){
  			boosters.put(s, storage.getInt(s));
		  }
	  }
	}

  public void save(){
  	storage.set("Boosters", null);
  	for(String s : boosters.keySet()){
  		storage.set("Boosters." + s, boosters.get(s));
	  }
	  try{
		  storage.save(playerFile);
	  }
	  catch(IOException e){
		  e.printStackTrace();
	  }
  }
}
