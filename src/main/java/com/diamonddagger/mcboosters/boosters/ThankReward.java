package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.util.parser.Parser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ThankReward{

	private static final String THANK_KEY = "Booster.ThankReward";

	@Getter
	@Setter
	private Parser vanillaExpReward;

	@Getter
	@Setter
	private Parser mcmmoExpReward;

	@Getter
	@Setter
	private Parser mcrpgExpReward;

	@Getter
	@Setter
	private List<String> thankerCommands;

	@Getter
	@Setter
	private List<String> ownerCommands;

	public ThankReward(FileConfiguration configuration){
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
			if(configuration.contains(THANK_KEY + ".ThankerCommands")){
				thankerCommands = configuration.getStringList(THANK_KEY + ".ThankerCommands");
			}
			if(configuration.contains(THANK_KEY + ".OwnerCommands")){
				ownerCommands = configuration.getStringList(THANK_KEY + ".OwnerCommands");
			}
		}
	}

}
