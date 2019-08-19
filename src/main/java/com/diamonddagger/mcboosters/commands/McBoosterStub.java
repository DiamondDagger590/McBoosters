package com.diamonddagger.mcboosters.commands;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.guis.FileGUI;
import com.diamonddagger.mcboosters.guis.GUITracker;
import com.diamonddagger.mcboosters.players.BoosterPlayer;
import com.diamonddagger.mcboosters.util.FileManager;
import com.diamonddagger.mcboosters.util.Methods;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class McBoosterStub implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
    McBoosters plugin = McBoosters.getInstance();
    FileConfiguration config = plugin.getLangFile();
    if(sender instanceof Player){
      Player p = (Player) sender;
      if(args.length == 0){
        BoosterPlayer bp = McBoosters.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
        FileGUI fileGUI = new FileGUI("Home", McBoosters.getInstance().getFileManager().getFile(FileManager.Files.HOME_GUI), bp);
        p.openInventory(fileGUI.getGui().getInv());
        GUITracker.trackPlayer(p, fileGUI);
        return true;
      }
      else{
        // /mcbooster admin give %player% mixedbooster 1
        if(args.length < 5){
          p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.HelpPrompt")));
          return true;
        }
        else{
          if(args[0].equalsIgnoreCase("admin")){
            if(args[1].equalsIgnoreCase("give")){
              if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.admin.*") || p.hasPermission("mcbooster.admin.give")){
                if(Methods.hasPlayerLoggedInBefore(args[2])){
                  if(McBoosters.getInstance().getBoosterManager().isBooster(args[3])){
                    if(Methods.isInt(args[4])){
                      OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                      if(McBoosters.getInstance().getPlayerManager().isPlayerStored(target.getUniqueId())){
                        McBoosters.getInstance().getPlayerManager().getPlayer(target.getUniqueId()).giveBoosters(args[3], Integer.parseInt(args[4]));
                        p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() +
                                McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersGiven".replace("%Player%", target.getName())
                                        .replace("%Amount%", args[4]).replace("%BoosterType%", args[3]))));
                        if(target.isOnline()){
                          ((Player) target).sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
                                  + McBoosters.getInstance().getLangFile().getString("Messages.Admin.Received").replace("%Amount%", args[4])
                          .replace("%BoosterType%", args[3])));
                        }
                        return true;
                      }
                      else{
                        BoosterPlayer boosterPlayer = new BoosterPlayer(target);
                        boosterPlayer.giveBoosters(args[3], Integer.parseInt(args[4]));
                        p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() +
                                McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersGiven".replace("%Player%", target.getName())
                                        .replace("%Amount%", args[4]).replace("%BoosterType%", args[3]))));
                        return true;
                      }
                    }
                    else{
                      p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.NotAnInt")));
                      return true;
                    }
                  }
                  else{
                    p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.NotABooster")
                    .replace("%BoosterType%", args[3])));
                    return true;
                  }
                }
                else{
                  p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.PlayerNotLoggedIn")));
                  return true;
                }
              }
              else{
                p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.NoPerms")));
                return true;
              }
            }
          }
        }
      }

    }
    return false;
  }
}
