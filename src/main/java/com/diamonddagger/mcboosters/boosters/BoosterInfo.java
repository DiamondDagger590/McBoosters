package com.diamonddagger.mcboosters.boosters;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
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
      List<String> ownerCommands = boosterFile.contains(boosterName + ".CommandTimer.OwnerCommandsToRun") ? boosterFile.getStringList(boosterName + ".CommandTimer.OwnerCommandsToRun") : new ArrayList<>();
      
      List<TimerCommand> timerCommands = new ArrayList<>();
      List<TimerCommand> ownerTimerCommands = new ArrayList<>();
      
      for(String command : commands){
        String[] data = command.split(":");
        
        timerCommands.add(new TimerCommand(data[0], data.length > 1 ? Integer.parseInt(data[1]) : 1));
      }
      
      for(String ownerCommand : ownerCommands){
        String[] data = ownerCommand.split(":");
  
        ownerTimerCommands.add(new TimerCommand(data[0], data.length > 1 ? Integer.parseInt(data[1]) : 1));
      }
      
      boolean useAmountParam = boosterFile.getBoolean(boosterName + ".CommandTimer.UseAmountParam", false);
      boosterCommandTimer = new BoosterCommandTimer(commandFrequency, timerCommands, ownerTimerCommands, boosterName, useAmountParam);
    }
    this.boostWrapper = new BoostWrapper(boosterFile, boosterName);
  }
}
