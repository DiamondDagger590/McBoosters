package com.diamonddagger.mcboosters.boosters;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
  @Getter
  private BoosterCommandTimer boosterCommandTimer;
  @Getter
  private boolean isDiscordSupportEnabled;
  @Getter
  private String startChannel;
  @Getter
  private String endChannel;

  public BoosterInfo(FileConfiguration boosterFile, String boosterName){
    this.thankReward = new ThankReward(boosterFile, boosterName);
    this.displayName = boosterFile.getString(boosterName + ".DisplayName");
    this.maxAmount = boosterFile.getInt(boosterName + ".MaxAmount", 1);
    this.duration = boosterFile.getInt(boosterName + ".Duration");
    this.isDiscordSupportEnabled = boosterFile.contains(boosterName + ".DiscordSupport") && boosterFile.getBoolean(boosterName + ".DiscordSupport.Enabled", false);
    if(isDiscordSupportEnabled){
      startChannel = boosterFile.getString(boosterName + ".DiscordSupport.StartChannel");
      endChannel = boosterFile.getString(boosterName + ".DiscordSupport.EndChannel");
    }
    if(boosterFile.contains(boosterName + ".CommandTimer")){
      int commandFrequency = boosterFile.getInt(boosterName + ".CommandTimer.CommandFrequency");
      List<String> commands = boosterFile.getStringList(boosterName + ".CommandTimer.CommandsToRun");
      boosterCommandTimer = new BoosterCommandTimer(commandFrequency, commands, boosterName);
    }
    this.boostWrapper = new BoostWrapper(boosterFile, boosterName);
  }
}
