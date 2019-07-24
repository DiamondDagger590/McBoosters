package com.diamonddagger.mcboosters.players;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BoosterPlayer {

    @Getter
    private Player player;

    @Getter
    private UUID uuid;
}
