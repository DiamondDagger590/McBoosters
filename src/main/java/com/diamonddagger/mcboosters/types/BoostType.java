package com.diamonddagger.mcboosters.types;

import lombok.Getter;

public enum BoostType{

	MCRPG("McRPG"),
	VANILLA("Vanilla"),
	MCMMO("McMMO"),
	JOBS("Jobs");

	@Getter
	private String name;

	BoostType(String name){
		this.name = name;
	}
}
