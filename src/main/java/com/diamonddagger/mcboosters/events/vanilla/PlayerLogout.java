package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class PlayerLogout implements Listener{

	private static HashMap<UUID, BukkitTask> playerLogOutTasks = new HashMap<>();

	public static boolean hasPlayer(UUID uuid){
		return playerLogOutTasks.containsKey(uuid);
	}

	public static void cancelRemove(UUID uuid){
		if(playerLogOutTasks.containsKey(uuid)){
			playerLogOutTasks.remove(uuid).cancel();
		}
	}

	@EventHandler
	public void quitEvent(PlayerQuitEvent e){
		Player p = e.getPlayer();
		PlayerManager playerManager = McBoosters.getInstance().getPlayerManager();
		BukkitTask task =  new BukkitRunnable(){
			@Override
			public void run(){
				if(!p.isOnline() && playerManager.isPlayerStored(p.getUniqueId())){
					playerManager.removePlayer(p.getUniqueId());
				}
				playerLogOutTasks.remove(p.getUniqueId());
			}
		}.runTaskLater(McBoosters.getInstance(), 5 * 1200);
		playerLogOutTasks.put(p.getUniqueId(), task);
	}
}
