package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerLogin implements Listener {

  @EventHandler
  public void login(PlayerLoginEvent e){
    PlayerManager playerManager = McBoosters.getInstance().getPlayerManager();
    if(playerManager.isPlayerStored(e.getPlayer().getUniqueId())){
      PlayerLogout.cancelRemove(e.getPlayer().getUniqueId());
    }
    else{
      playerManager.initializePlayer(e.getPlayer());
    }
    new BukkitRunnable() {
      @Override
      public void run(){
        McBoosters.getInstance().getBoosterManager().thankAllBoosters(e.getPlayer());
      }
    }.runTaskLater(McBoosters.getInstance(), 40);
  }
}
