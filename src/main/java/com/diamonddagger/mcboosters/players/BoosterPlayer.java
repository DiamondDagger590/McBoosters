package com.diamonddagger.mcboosters.players;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BoosterPlayer {

  @Getter
  private Player player;

  @Getter
  private UUID uuid;

  private HashMap<String, Integer> boosters;

  BoosterPlayer(Player p){
    this.player = p;
    this.uuid = p.getUniqueId();
  }

  public boolean doesPlayerHaveBooster(String booster){
  	return boosters.containsKey(booster);
  }

  public boolean doesPlayerHaveAnyBoosters(){
  	return boosters.values().stream().anyMatch(i -> i > 0);
  }

  public int getBoosterAmount(String booster){
  	return boosters.getOrDefault(booster, 0);
  }

  public void setBoosterAmount(String boosterType, int amount){
  	boosters.put(boosterType, amount);
  }

  public void decrementBoosterAmount(String boosterType){
  	if(boosters.containsKey(boosterType)){
  		int amount = boosters.get(boosterType);
  		if(amount - 1 <= 0){
  			boosters.remove(boosterType);
  		}
  		else{
  			boosters.replace(boosterType, amount - 1);
		  }
	  }
  }
}
