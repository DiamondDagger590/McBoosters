package com.diamonddagger.mcboosters.events.vanilla;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.Booster;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.eunoians.mcrpg.api.events.mcrpg.McRPGPlayerExpGainEvent;

public class McRPGExpEvent implements Listener{

	@EventHandler
	public void expGain(McRPGPlayerExpGainEvent e){
		BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
		for(Booster booster : boosterManager.getActiveBoosters()){
			e.setExpGained((int) (e.getExpGained() * booster.getBoostWrapper().getMcMMOBoost(e.getSkillGained().getName())));
		}
	}
}
