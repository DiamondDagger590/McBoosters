package com.diamonddagger.mcboosters.boosters;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class BoosterInfo {

  @Getter
  private ThankReward thankReward;
  @Getter
  private String displayName;
  @Getter
  private int maxAmount;
  @Getter
  private int duration;
  @Getter
  private BoostWrapper boostWrapper;

  public BoosterInfo(FileConfiguration boosterFile, String boosterName){
    this.thankReward = new ThankReward(boosterFile, boosterName);
    this.displayName = boosterFile.getString(boosterName + ".DisplayName");
    this.maxAmount = boosterFile.getInt(boosterName + ".MaxAmount", 1);
    this.duration = boosterFile.getInt(boosterName + ".Duration");
    this.boostWrapper = new BoostWrapper(boosterFile, boosterName);
  }
}
