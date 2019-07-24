package com.diamonddagger.mcboosters.guis;

import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

public class QueueGUI extends GUI{



	private GUIInventoryFunction guiBuildFunction;


	public QueueGUI(String name, FileConfiguration file, BoosterPlayer player){
		super(new GUIBuilder(name, file, player));
		this.getGui().replacePlaceHolders();

		if(!GUITracker.isPlayerTracked(player)){
			GUITracker.trackPlayer(player, this);
		}

		guiBuildFunction = (GUIBuilder builder) -> {
			Inventory inv = Bukkit.createInventory(null, 54, Methods.color(file.getString("DisplayName")));
			return inv;
		};
		this.getGui().setBuildGUIFunction(guiBuildFunction);
		this.getGui().rebuildGUI();
	}


}
