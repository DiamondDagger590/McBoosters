package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.util.parser.Parser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ThankReward{

	@Getter
	@Setter
	private Parser vanillaExpReward = new Parser("0");

	@Getter
	@Setter
	private Parser mcmmoExpReward = new Parser("0");

	@Getter
	@Setter
	private Parser mcrpgExpReward = new Parser("0");

	@Getter
	@Setter
	private Parser jobsMoneyReward = new Parser("0");

	@Getter
	@Setter
	private List<String> thankerCommands = new ArrayList<>();

	@Getter
	@Setter
	private List<String> ownerCommands = new ArrayList<>();

	public ThankReward(FileConfiguration configuration, String booster){
		final String THANK_KEY = booster + ".ThankingRewards";

		if(configuration.contains(THANK_KEY)){
			if(configuration.contains(THANK_KEY + ".McRPGExp")){
				mcrpgExpReward = new Parser(configuration.getString(THANK_KEY + ".McRPGExp"));
			}
			if(configuration.contains(THANK_KEY + ".VanillaExp")){
				vanillaExpReward = new Parser(configuration.getString(THANK_KEY + ".VanillaExp"));
			}
			if(configuration.contains(THANK_KEY + ".McMMOExp")){
				mcmmoExpReward = new Parser(configuration.getString(THANK_KEY + ".McMMOExp"));
			}
			if(configuration.contains(THANK_KEY + ".JobsMoney")){
				jobsMoneyReward = new Parser(configuration.getString(THANK_KEY + ".JobsMoney"));
			}
			if(configuration.contains(THANK_KEY + ".ThankerCommands")){
				thankerCommands = configuration.getStringList(THANK_KEY + ".ThankerCommands");
			}
			if(configuration.contains(THANK_KEY + ".OwnerCommands")){
				ownerCommands = configuration.getStringList(THANK_KEY + ".OwnerCommands");
			}
		}
	}

}
