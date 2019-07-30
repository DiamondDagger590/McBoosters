package com.diamonddagger.mcboosters.players;

import com.diamonddagger.mcboosters.boosters.Booster;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BoosterPlayer {

  @Getter
  private Player player;

  @Getter
  private UUID uuid;

  private HashMap<Booster, Integer> boosters;

  private BoosterPlayer(Player p){
    this.player = p;
    this.uuid = p.getUniqueId();
  }
}
