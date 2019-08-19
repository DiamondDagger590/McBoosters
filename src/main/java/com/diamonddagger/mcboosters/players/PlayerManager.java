package com.diamonddagger.mcboosters.players;

import com.diamonddagger.mcboosters.McBoosters;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private Map<UUID, BoosterPlayer> players = new HashMap<>();
    private BukkitTask saveTask;

    public PlayerManager(){
        File playerDataFolder = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata");
        if(!playerDataFolder.exists()){
            playerDataFolder.mkdir();
        }
        startSaveTask();
    }

    public void restartSaveTask(){
        if(saveTask != null){
            saveTask.cancel();
        }
        startSaveTask();
    }

    public void initializePlayer(Player p){
        if(!players.containsKey(p.getUniqueId())){
            BoosterPlayer boosterPlayer = new BoosterPlayer(p);
            players.put(p.getUniqueId(), boosterPlayer);
        }
    }

    public void removePlayer(UUID uuid){
        players.remove(uuid);
    }

    public boolean isPlayerStored(UUID uuid){
        return players.containsKey(uuid);
    }

    public BoosterPlayer getPlayer(UUID uuid){
        return players.get(uuid);
    }

    private void startSaveTask(){
        saveTask = new BukkitRunnable(){
            @Override
            public void run(){
                for(BoosterPlayer bp : players.values()){
                    bp.save();
                }
            }
        }.runTaskTimerAsynchronously(McBoosters.getInstance(), 120L, 5 * 60 * 20);
    }
}
