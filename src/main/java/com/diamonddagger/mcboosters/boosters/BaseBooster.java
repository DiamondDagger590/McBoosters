package com.diamonddagger.mcboosters.boosters;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
