package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.Booster;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class KillEvent implements Listener{

	@EventHandler(priority = EventPriority.NORMAL)
	public void killEvent(EntitySpawnEvent e){
		BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
		for(Booster booster : boosterManager.getActiveBoosters()){
			//TODO
		}
	}
}
