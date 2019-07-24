package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.guis.GUI;
import com.diamonddagger.mcboosters.guis.GUIEventBinder;
import com.diamonddagger.mcboosters.guis.GUITracker;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.players.PlayerManager;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public class InvClick implements Listener{

	@EventHandler
	private void invClick(InventoryClickEvent e){
		Player p = (Player) e.getWhoClicked();
		FileConfiguration langFile = McBoosters.getInstance().getLangFile();
		//If this is a gui
		if(GUITracker.isPlayerTracked(p)){
			//Cancel event
			e.setCancelled(true);
			//Ignore player inventory
			if(e.getClickedInventory() instanceof PlayerInventory){
				return;
			}
			BoosterPlayer mp = McBoosters.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
			//Cuz null errors are fun
			if(e.getCurrentItem() == null){
				return;
			}
			GUI currentGUI = GUITracker.getPlayersGUI(p);
			GUIEventBinder binder = null;
			if(currentGUI.getGui().getBoundEvents() != null){
				binder = currentGUI.getGui().getBoundEvents().stream().filter(guiBinder -> guiBinder.getSlot() == e.getSlot()).findFirst().orElse(null);
			}
			if(binder == null){
				return;
			}
			for(String eventBinder : binder.getBoundEventList()){
				String[] events = eventBinder.split(":");
				String event = events[0];
				if(event.equalsIgnoreCase("Permission")){
					String perm = events[1];
					if(!p.hasPermission(perm)){
						GUITracker.stopTrackingPlayer(p);
						p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + langFile.getString("Messages.Commands.Utility.NoPerms")));
						return;
					}
					else{
						continue;
					}
				}
				else if(event.equalsIgnoreCase("Command")){
					String sender = events[1];
					if(sender.equalsIgnoreCase("console")){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), events[2].replace("%Player%", p.getName()));
						continue;
					}
					else if(sender.equalsIgnoreCase("player")){
						p.performCommand(events[2]);
						continue;
					}
				}
				else if(event.equalsIgnoreCase("close")){
					GUITracker.stopTrackingPlayer(p);
					p.closeInventory();
					continue;
				}
				else if(event.equalsIgnoreCase("back")){
					if(GUITracker.doesPlayerHavePrevious(p)){
						GUI previousGUI = GUITracker.getPlayersPreviousGUI(p);
						previousGUI.setClearData(true);
						currentGUI.setClearData(false);
						p.openInventory(previousGUI.getGui().getInv());
						GUITracker.replacePlayersGUI(p, previousGUI);
						continue;
					}
					else{
						GUITracker.stopTrackingPlayer(p);
						continue;
					}
				}
				else if(event.equalsIgnoreCase("Open")){
					GUITracker.stopTrackingPlayer(p);
					p.closeInventory();
					p.sendMessage(Methods.color("&cThis has yet to be implemented"));
					return;
				}
				else if(event.equalsIgnoreCase("OpenFile")){
					GUI gui = null;
					currentGUI.setClearData(false);
					p.openInventory(gui.getGui().getInv());
					GUITracker.replacePlayersGUI(mp, gui);
					return;
				}
			}
		}
	}
}
