package com.diamonddagger.mcboosters.api.events;

import com.diamonddagger.mcboosters.boosters.Booster;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

public class BoosterEndEvent extends BoosterEvent{


  @Getter
  private OfflinePlayer owner;

  @Getter
  private Booster booster;

  public BoosterEndEvent(OfflinePlayer owner, Booster booster){
    this.owner = owner;
    this.booster = booster;
  }
}
