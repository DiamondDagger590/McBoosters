package com.diamonddagger.mcboosters.commands;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class McBoosterStub implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		McBoosters plugin = McBoosters.getInstance();
		FileConfiguration config = plugin.getLangFile();
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length > 0){
				if(args[0].equalsIgnoreCase("activate")){
					if(args.length > 1){
						if(McBoosters.getInstance().getBoosterManager().isABooster(args[1].toLowerCase())){
							BoosterPlayer bp = McBoosters.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
							if(bp.doesPlayerHaveBooster(args[1].toLowerCase()) && bp.getBoosterAmount(args[1].toLowerCase()) >= 1){
								if(McBoosters.getInstance().getBoosterManager().activateBooster(bp, args[1].toLowerCase())){
									bp.decrementBoosterAmount(args[1].toLowerCase());
								}
								else{
									p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&cYou are currently unable to activate a booster, there might be too many active currently. Please try again later."));
									return true;
								}
								//TODO
							}
							else{
								p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&cYou do not have enough of that type of booster to activate one."));
								return true;
							}
						}
						else{
							p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&cYou did not enter a valid booster type."));
							return true;
						}
					}
				}
				if(args[0].equalsIgnoreCase("admin")){
					if(args.length >= 4){
						// /mcbooster admin give DiamondDagger590 mixedbooster 2
						if(args[1].equalsIgnoreCase("give")){
							if(Methods.hasPlayerLoggedInBefore(args[2])){
								if(plugin.getBoosterManager().isABooster(args[3])){
									if(Methods.isInt(args[4])){
										if(!(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.admin.*") || p.hasPermission("mcbooster.admin.give"))){
											p.sendMessage(Methods.color(plugin.getPluginPrefix() + "&cYou do not have permission to execute this command."));
											return true;
										}
										int amount = Integer.parseInt(args[4]);
										BoosterPlayer bp;
										OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
										if(target.isOnline()){
											bp = plugin.getPlayerManager().getPlayer(p.getUniqueId());
										}
										else{
											bp = new BoosterPlayer(target.getUniqueId());
										}
										bp.giveBoosters(args[3], amount);
										bp.save();
										//TODO add message
										if(target.isOnline()){
											target.getPlayer().sendMessage(Methods.color(plugin.getPluginPrefix() + "&aYou were given %Amount% %BoosterType% boosters.".replace("%Amount%", Integer.toString(amount))
															.replace("%BoosterType%", args[2])));
										}
										p.sendMessage(Methods.color(plugin.getPluginPrefix() + "&aYou gave %Amount% %BoosterType% boosters.".replace("%Amount%", Integer.toString(amount))
														.replace("%BoosterType%", args[2])));
										return true;
									}
									else{
										p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&cThe argument you entered is not an integer"));
										return true;
									}
								}
								else{
									p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + "&c%BoosterType% is not a valid booster. Either reload the plugin or create a folder with for that booster"));
									return true;
								}
							}
						}
						else{
							//TODO
						}
					}
				}
			}
			else{
				//TODO open GUI
				return true;
			}
		}
		return false;
	}
}
