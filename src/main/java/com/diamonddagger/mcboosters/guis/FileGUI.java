package com.diamonddagger.mcboosters.guis;

import com.diamonddagger.mcboosters.players.BoosterPlayer;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class FileGUI extends GUI{

	@Getter
	private String name;

	public FileGUI(String name, FileConfiguration file, BoosterPlayer player){
		super(new GUIBuilder(name, file, player));
		this.getGui().replacePlaceHolders();
		if(!GUITracker.isPlayerTracked(player)){
			GUITracker.trackPlayer(player, this);
		}
	}
}
