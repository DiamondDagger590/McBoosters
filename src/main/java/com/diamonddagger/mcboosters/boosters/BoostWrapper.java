package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BoostWrapper {

  private Map<String, Double> vanillaBoosts = new HashMap<>();
  private Map<String, Double> mcmmoBoosts = new HashMap<>();
  private Map<String, Double> mcRPGBoosts = new HashMap<>();
  private Map<String, Double> jobsExpBoost = new HashMap<>();
  private double jobsMoneyBoost = 1.0;

  BoostWrapper(FileConfiguration boosterFile, String boosterName){
    final String BASE_KEY = boosterName + ".BoostTypes.";
    final String VANILLA_KEY = BASE_KEY + "Vanilla";
    final String MCRPG_KEY = BASE_KEY + "McRPG";
    final String MCMMO_KEY = BASE_KEY + "McMMO";
    final String JOBS_EXP_KEY = BASE_KEY + "JobExp";
    final String JOBS_MONEY_KEY = BASE_KEY + "JobsMoney";

    if(boosterFile.contains(VANILLA_KEY)){
      for(String s : boosterFile.getStringList(VANILLA_KEY)){
        String[] data = s.split(":");
        vanillaBoosts.put(data[0], Double.parseDouble(data[1]));
      }
    }
    if(boosterFile.contains(MCRPG_KEY)){
      for(String s : boosterFile.getStringList(MCRPG_KEY)){
        String[] data = s.split(":");
        mcRPGBoosts.put(data[0], Double.parseDouble(data[1]));
      }
    }
    if(boosterFile.contains(MCMMO_KEY)){
      for(String s : boosterFile.getStringList(MCMMO_KEY)){
        String[] data = s.split(":");
        mcmmoBoosts.put(data[0], Double.parseDouble(data[1]));
      }
    }
    if(boosterFile.contains(JOBS_EXP_KEY)){
      for(String s : boosterFile.getStringList(JOBS_EXP_KEY)){
        String[] data = s.split(":");
        jobsExpBoost.put(data[0], Double.parseDouble(data[1]));
      }
    }
    if(boosterFile.contains(JOBS_MONEY_KEY)){
      jobsMoneyBoost = boosterFile.getDouble(JOBS_MONEY_KEY);
    }
    if(vanillaBoosts.isEmpty() && mcRPGBoosts.isEmpty() && mcmmoBoosts.isEmpty() && jobsExpBoost.isEmpty() && !boosterFile.contains(BASE_KEY + "ThankCommands")){
      Bukkit.getLogger().log(Level.WARNING, Methods.color("&cBooster named: " + boosterFile.getString(boosterName + ".DisplayName") + " has no valid boosts associated with it. This might be due" +
              "to it being a command reward based booster. If this is not the case, please correct the booster or seek assistance on discord."));
    }
  }

  public double getVanillaBoost(String type){
    return vanillaBoosts.containsKey("ALL") ? vanillaBoosts.get("ALL") : vanillaBoosts.getOrDefault(type, 1.0);
  }

  public double getMcMMOBoost(String skill){
    return mcmmoBoosts.containsKey("ALL") ? mcmmoBoosts.get("ALL") : mcmmoBoosts.getOrDefault(skill, 1.0);
  }

  public double getMcRPGBoost(String skill){
    return mcRPGBoosts.containsKey("ALL") ? mcRPGBoosts.get("ALL") :mcRPGBoosts.getOrDefault(skill, 1.0);
  }

  public double getJobsExpBoost(String job){
    return jobsExpBoost.containsKey("ALL") ? jobsExpBoost.get("ALL") : jobsExpBoost.getOrDefault(job, 1.0);
  }

  public double getJobsMoneyBoost(){
    return jobsMoneyBoost;
  }

}