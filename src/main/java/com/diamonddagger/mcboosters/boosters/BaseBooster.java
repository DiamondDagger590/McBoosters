package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.util.parser.Parser;
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

public class BaseBooster implements Booster{

	@Getter
	private BoostWrapper boostWrapper;
	@Getter
	private ThankReward thankReward;
	private UUID boosterOwner;
	private long endTime;
	@Getter
	private Set<UUID> thankedPlayers;

	public BaseBooster(UUID boosterOwner, BoostWrapper boostWrapper, ThankReward thankReward, long endTime){
		this.boosterOwner = boosterOwner;
		this.boostWrapper = boostWrapper;
		this.thankReward = thankReward;
		this.endTime = endTime;
		this.thankedPlayers = new HashSet<>();
	}

	public BaseBooster(BoostWrapper boostWrapper, ThankReward thankReward, FileConfiguration backupFile, String key){
		this.boosterOwner = UUID.fromString(backupFile.getString(key + ".Owner"));
		this.boostWrapper = boostWrapper;
		this.thankReward = thankReward;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, backupFile.getInt(key + ".TimeLeft"));
		this.endTime = cal.getTimeInMillis();
		this.thankedPlayers = backupFile.getStringList(key + ".ThankedPlayers").stream().map(UUID::fromString).collect(Collectors.toSet());
	}


	public void thank(Player thanker){
		OfflinePlayer owner = Bukkit.getOfflinePlayer(boosterOwner);
		int vanillaExp = (int) thankReward.getVanillaExpReward().getValue();
		int mcrpgExp = 0;
		if(McBoosters.getInstance().isMcrpgEnabled()){
			try{
				McRPGPlayer mp = PlayerManager.getPlayer(thanker.getUniqueId());
				Parser p = thankReward.getMcrpgExpReward();
				p.setVariable("power_level", mp.getPowerLevel());
				mcrpgExp = (int) p.getValue();
				mp.giveRedeemableExp(mcrpgExp);
				mp.saveData();
			}
			catch(McRPGPlayerNotFoundException e){
				return;
			}
		}
		thanker.giveExp(vanillaExp);
		if(owner.isOnline()){
			Player onlineOwner = (Player) owner;
			onlineOwner.giveExp(vanillaExp);
			if(mcrpgExp > 0){
				try{
					McRPGPlayer mp = PlayerManager.getPlayer(onlineOwner.getUniqueId());
					mp.giveRedeemableExp(mcrpgExp);
					mp.saveData();
				}
				catch(McRPGPlayerNotFoundException e){
					e.printStackTrace();
				}
			}
		}
		else{
			File playerStorageFile = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerstorage.yml");
			FileConfiguration storage = YamlConfiguration.loadConfiguration(playerStorageFile);
			String key = "Players." + boosterOwner.toString();
			if(storage.contains("Players." + boosterOwner.toString())){
				if(vanillaExp > 0){
					if(storage.contains(key + ".VanillaExp")){
						storage.set(key + ".VanillaExp", vanillaExp + storage.getInt(key + ".VanillaExp"));
					}
					else{
						storage.set(key + ".VanillaExp", vanillaExp);
					}
				}
				if(mcrpgExp > 0){
					if(storage.contains(key + ".McRPGExp")){
						storage.set(key + ".McRPGExp", vanillaExp + storage.getInt(key + ".McRPGExp"));
					}
					else{
						storage.set(key + ".McRPGExp", mcrpgExp);
					}
				}
			}
			else{
				if(vanillaExp > 0){
					storage.set(key + ".VanillaExp", vanillaExp);
				}
				if(mcrpgExp > 0){
					storage.set(key + ".McRPGExp", mcrpgExp);
				}
			}
			try{
				storage.save(playerStorageFile);
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
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
