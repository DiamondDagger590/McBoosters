package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.util.parser.Parser;
import lombok.Getter;
import lombok.Setter;

public class ThankReward{

	@Getter
	@Setter
	private Parser vanillaExpReward;

	@Getter
	@Setter
	private Parser mcmmoExpReward;

	@Getter
	@Setter
	private Parser mcrpgExpReward;


	public ThankReward(Parser vanillaExpReward, Parser mcmmoExpReward, Parser mcrpgExpReward){
		this.vanillaExpReward = vanillaExpReward;
		this.mcmmoExpReward = mcmmoExpReward;
		this.mcrpgExpReward = mcrpgExpReward;
	}

}
