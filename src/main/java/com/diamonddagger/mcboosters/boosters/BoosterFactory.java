package com.diamonddagger.mcboosters.boosters;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Calendar;
import java.util.UUID;

class BoosterFactory {

  static Booster getBooster(FileConfiguration config, UUID owner, String boosterName){
    BoosterInfo boosterInfo = new BoosterInfo(config, boosterName);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, boosterInfo.getDuration());
    long endTime = calendar.getTimeInMillis();
    return new BaseBooster(owner, endTime, boosterInfo);
  }

  static Booster getBooster(FileConfiguration boosterFile, FileConfiguration backupFile, String key, String boosterName){
    return new BaseBooster(backupFile, key, new BoosterInfo(boosterFile, boosterName));
  }
}
