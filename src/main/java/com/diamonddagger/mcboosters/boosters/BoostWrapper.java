package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.util.Methods;
import com.diamonddagger.mcboosters.util.parser.Parser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BoostWrapper{

	private Map<String, Double> vanillaBoosts = new HashMap<>();
	private Map<String, Double> mcmmoBoosts = new HashMap<>();
	private Map<String, Double> mcRPGBoosts = new HashMap<>();
	private Map<String, Double> jobsExpBoost = new HashMap<>();
	private Map<String, Double> jobsMoneyBoost = new HashMap<>();

	@Getter private Parser vanillaStackBoosts;
	@Getter private Parser mcmmoStackBoosts;
	@Getter private Parser mcrpgStackBoosts;
	@Getter private Parser jobsExpStackBoosts;
	@Getter private Parser jobsMoneyStackBoosts;


	private static final String BASE_KEY = "Booster.Boosts.";
	private static final String VANILLA_KEY = BASE_KEY + "Vanilla.Sources";
	private static final String MCRPG_KEY = BASE_KEY + "McRPG.Skills";
	private static final String MCMMO_KEY = BASE_KEY + "McMMO.Skills";
	private static final String JOBS_EXP_KEY = BASE_KEY + "JobExp.Sources";
	private static final String JOBS_MONEY_KEY = BASE_KEY + "JobsMoney.Sources";

	BoostWrapper(FileConfiguration boosterFile){
		if(boosterFile.contains(VANILLA_KEY)){
			for(String s : boosterFile.getConfigurationSection(VANILLA_KEY).getKeys(false)){
				double boost = boosterFile.getDouble(VANILLA_KEY + "."+ s);
				vanillaBoosts.put(s, boost);
			}
			if(boosterFile.contains(BASE_KEY + "Vanilla.StackEquation")){
				vanillaStackBoosts = new Parser(boosterFile.getString(BASE_KEY + "Vanilla.StackEquation"));
			}
		}
		if(boosterFile.contains(MCRPG_KEY)){
			for(String s : boosterFile.getConfigurationSection(MCRPG_KEY).getKeys(false)){
				double boost = boosterFile.getDouble(MCRPG_KEY + "." + s);
				mcRPGBoosts.put(s, boost);
			}
			if(boosterFile.contains(BASE_KEY + "McRPG.StackEquation")){
				mcrpgStackBoosts = new Parser(boosterFile.getString(BASE_KEY + "McRPG.StackEquation"));
			}
		}
		if(boosterFile.contains(MCMMO_KEY)){
			for(String s : boosterFile.getConfigurationSection(MCMMO_KEY).getKeys(false)){
				double boost = boosterFile.getDouble(MCMMO_KEY + "." + s);
				mcmmoBoosts.put(s, boost);
			}
			if(boosterFile.contains(BASE_KEY + "McMMO.StackEquation")){
				mcmmoStackBoosts = new Parser(boosterFile.getString(BASE_KEY + "McMMO.StackEquation"));
			}
		}
		if(boosterFile.contains(JOBS_EXP_KEY)){
			for(String s : boosterFile.getConfigurationSection(JOBS_EXP_KEY).getKeys(false)){
				double boost = boosterFile.getDouble(JOBS_EXP_KEY + "." + s);
				jobsExpBoost.put(s, boost);
			}
			if(boosterFile.contains(BASE_KEY + "JobsExp.StackEquation")){
				jobsExpStackBoosts = new Parser(boosterFile.getString(BASE_KEY + "JobsExp.StackEquation"));
			}
		}
		if(boosterFile.contains(JOBS_MONEY_KEY)){
			for(String s : boosterFile.getConfigurationSection(JOBS_MONEY_KEY).getKeys(false)){
				double boost = boosterFile.getDouble(JOBS_MONEY_KEY + "." +  s);
				jobsMoneyBoost.put(s, boost);
			}
			if(boosterFile.contains(BASE_KEY + "JobsMoney.StackEquation")){
				jobsMoneyStackBoosts = new Parser(boosterFile.getString(BASE_KEY + "JobsMoney.StackEquation"));
			}
		}
		if(vanillaBoosts.isEmpty() && mcRPGBoosts.isEmpty() && mcmmoBoosts.isEmpty() && jobsMoneyBoost.isEmpty() && jobsExpBoost.isEmpty()){
			Bukkit.getLogger().log(Level.WARNING, Methods.color("&cBooster named: " + boosterFile.getString("Booster.Name") + " has no valid boosts associated with it. Please either remove this booster, fix invalid criteria or seek support on Discord"));
		}
	}

	public double getVanillaBoost(String type){
		return vanillaBoosts.getOrDefault(type, 1.0);
	}

	public double getMcMMOBoost(String skill){
		return mcmmoBoosts.getOrDefault(skill, 1.0);
	}

	public double getMcRPGBoost(String skill){
		return mcRPGBoosts.getOrDefault(skill, 1.0);
	}

	public double getJobsExpBoost(String job){
		return jobsExpBoost.getOrDefault(job, 1.0);
	}

	public double getJobsMoneyBoost(String job){
		return jobsMoneyBoost.getOrDefault(job, 1.0);
	}

}
