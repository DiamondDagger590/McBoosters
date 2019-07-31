package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.McBoosters;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Calendar;
import java.util.UUID;

class BoosterFactory{

	static Booster getBooster(FileConfiguration config, UUID owner){
		McBoosters mcBoosters = McBoosters.getInstance();

		BoostWrapper boostWrapper = new BoostWrapper(config);
		Calendar calendar = Calendar.getInstance();
	  calendar.add(Calendar.SECOND, config.getInt("Booster.Duration", 10));
	  long endTime = calendar.getTimeInMillis();
	  ThankReward thankReward = new ThankReward(config);
		return new BaseBooster(owner, boostWrapper, thankReward, endTime);
	}

	static Booster getBooster(FileConfiguration boosterFile, FileConfiguration backupFile, String key){
		McBoosters mcBoosters = McBoosters.getInstance();

		BoostWrapper boostWrapper = new BoostWrapper(boosterFile);
		ThankReward thankReward = new ThankReward(boosterFile);
		return new BaseBooster(boostWrapper, thankReward, backupFile, key);
	}
}
