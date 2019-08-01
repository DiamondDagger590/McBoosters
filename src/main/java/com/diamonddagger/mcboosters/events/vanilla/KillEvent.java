package com.diamonddagger.mcboosters.events.vanilla;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillEvent implements Listener{

	@EventHandler(priority = EventPriority.NORMAL)
	public void killEvent(EntityDamageByEntityEvent e){

	}
}
