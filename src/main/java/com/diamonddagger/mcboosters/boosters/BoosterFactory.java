package com.diamonddagger.mcboosters.boosters;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Calendar;
import java.util.UUID;

class BoosterFactory {

  static Booster getBooster(UUID owner, BoosterInfo boosterInfo){
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, boosterInfo.getDuration());
    long endTime = calendar.getTimeInMillis();
    return new BaseBooster(owner, endTime, boosterInfo);
  }

  static Booster getBooster(FileConfiguration backupFile, String key, BoosterInfo boosterInfo){
    return new BaseBooster(backupFile, key, boosterInfo);
  }
}
