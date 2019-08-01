package com.diamonddagger.mcboosters.players;

import com.diamonddagger.mcboosters.McBoosters;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private Map<UUID, BoosterPlayer> players = new HashMap<>();
    private BukkitTask saveTask;

    public PlayerManager(){
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
        File file = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerstorage.yml");
        saveTask = new BukkitRunnable(){
            @Override
            public void run(){
                FileConfiguration storage = YamlConfiguration.loadConfiguration(file);
                for(UUID uuid : players.keySet()){
                    BoosterPlayer bp = players.get(uuid);
                    if(bp.doesPlayerHaveAnyBoosters()){
                        String key = "Players." + uuid.toString() + ".";
                        for(String s : McBoosters.getInstance().getBoosterManager().getAllBoosters()){
                            if(bp.doesPlayerHaveBooster(s)){
                                storage.set(key + s, bp.getBoosterAmount(s));
                            }
                        }
                    }
                }
                try{
                    storage.save(file);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsynchronously(McBoosters.getInstance(), 120L, 5 * 60 * 20);
    }
}
