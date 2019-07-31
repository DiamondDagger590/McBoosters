package com.diamonddagger.mcboosters.boosters;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface Booster {

  BoostWrapper getBoostWrapper();

  long getEndTime();

  UUID getOwner();

  Set<UUID> getThankedPlayers();

  void thank(Player thanker);
}
