package com.diamonddagger.mcboosters.commands;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
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
          if(args[0].equalsIgnoreCase("help")){
            for(String s : Methods.colorLore(McBoosters.getInstance().getLangFile().getStringList("Messages.Help"))){
              p.sendMessage(s);
            }
            return true;
          }
          if(args.length == 1){
            if(args[0].equalsIgnoreCase("debug")){
              BoosterPlayer bp = McBoosters.getInstance().getPlayerManager().getPlayer(p.getUniqueId());
              bp.setDebugMode(!bp.isDebugMode());
              p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + (bp.isDebugMode() ? "&aDebug Mode enabled" : "&cDebug Mode disabled")));
              return true;
            }
            else if(args[0].equalsIgnoreCase("reload")){
              if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.reload")){
                McBoosters.getInstance().getBoosterManager().reload(McBoosters.getInstance());
                McBoosters.getInstance().getFileManager().reloadFiles();
                p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Admin.Reload")));
                return true;
              }
              else{
                p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.NoPerms")));
                return true;
              }
            }
          }
          if(args.length == 2){
            if(args[0].equalsIgnoreCase("admin")){
              if(args[1].equalsIgnoreCase("check")){
                BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
                p.sendMessage(Methods.color("&bJobs Exp Boost: " + boosterManager.getJobsExpBoost("Miner")));
                p.sendMessage(Methods.color("&bJobs Money Boost: " + boosterManager.getJobsMoneyBoost()));
                p.sendMessage(Methods.color("&bMcRPG Exp Boost: " + boosterManager.getMcRPGBoost("Swords")));
                p.sendMessage(Methods.color("&bVanilla Exp Boost: " + boosterManager.getVanillaBoost("DIAMOND_ORE")));
                return true;
              }
            }
          }
          // /mcbooster admin cancel %type%
          else if(args.length == 3){
            if(args[0].equalsIgnoreCase("admin")){
              if((args[1].equalsIgnoreCase("cancel"))){
                if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.admin.*") || p.hasPermission("mcbooster.admin.cancel")){
                  if(McBoosters.getInstance().getBoosterManager().getActiveBoosters(args[2]).size() > 0){
                    McBoosters.getInstance().getBoosterManager().cancelBooster(args[2]);
                    return true;
                  }
                  else{
                    p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.NoneActive")));
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
                                McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersGiven").replace("%Player%", target.getName())
                                        .replace("%Amount%", args[4]).replace("%BoosterType%", args[3])));
                        if(target.isOnline()){
                          ((Player) target).sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
                                  + McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersReceived").replace("%Amount%", args[4])
                                  .replace("%BoosterType%", args[3])));
                        }
                        return true;
                      }
                      else{
                        BoosterPlayer boosterPlayer = new BoosterPlayer(target);
                        boosterPlayer.giveBoosters(args[3], Integer.parseInt(args[4]));
                        p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() +
                                McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersGiven").replace("%Player%", target.getName())
                                        .replace("%Amount%", args[4]).replace("%BoosterType%", args[3])));
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
    else{
      // /mcbooster admin give %player% mixedbooster 1
      if(args.length < 5){
        if(args[0].equalsIgnoreCase("help")){
          for(String s : Methods.colorLore(McBoosters.getInstance().getLangFile().getStringList("Messages.Help"))){
            sender.sendMessage(s);
          }
          return true;
        }
        if(args.length == 1){
          if(args[0].equalsIgnoreCase("reload")){
            McBoosters.getInstance().getFileManager().reloadFiles();
            McBoosters.getInstance().getBoosterManager().reload(McBoosters.getInstance());
            sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Admin.Reload")));
            return true;
          }
        }
        if(args.length == 2){
          if(args[0].equalsIgnoreCase("admin")){
            if(args[1].equalsIgnoreCase("check")){
              BoosterManager boosterManager = McBoosters.getInstance().getBoosterManager();
              sender.sendMessage(Methods.color("&bJobs Exp Boost: " + boosterManager.getJobsExpBoost("Miner")));
              sender.sendMessage(Methods.color("&bJobs Money Boost: " + boosterManager.getJobsMoneyBoost()));
              sender.sendMessage(Methods.color("&bMcRPG Exp Boost: " + boosterManager.getMcRPGBoost("Swords")));
              sender.sendMessage(Methods.color("&bVanilla Exp Boost: " + boosterManager.getVanillaBoost("DIAMOND_ORE")));
              return true;
            }
          }
        }
        // /mcbooster admin cancel %type%
        else if(args.length == 3){
          if(args[0].equalsIgnoreCase("admin")){
            if(args[1].equalsIgnoreCase("cancel")){
              if(McBoosters.getInstance().getBoosterManager().getActiveBoosters(args[2]).size() > 0){
                McBoosters.getInstance().getBoosterManager().cancelBooster(args[2]);
                return true;
              }
              else{
                sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Boosters.NoneActive")));
                return true;
              }
            }
          }
          sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.HelpPrompt")));
          return true;
        }
      }
      else{
        if(args[0].equalsIgnoreCase("admin")){
          if(args[1].equalsIgnoreCase("give")){
            if(Methods.hasPlayerLoggedInBefore(args[2])){
              if(McBoosters.getInstance().getBoosterManager().isBooster(args[3])){
                if(Methods.isInt(args[4])){
                  OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                  if(McBoosters.getInstance().getPlayerManager().isPlayerStored(target.getUniqueId())){
                    McBoosters.getInstance().getPlayerManager().getPlayer(target.getUniqueId()).giveBoosters(args[3], Integer.parseInt(args[4]));
                    sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() +
                            McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersGiven").replace("%Player%", target.getName())
                                    .replace("%Amount%", args[4]).replace("%BoosterType%", args[3])));
                    if(target.isOnline()){
                      ((Player) target).sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix()
                              + McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersReceived").replace("%Amount%", args[4])
                              .replace("%BoosterType%", args[3])));
                    }
                    return true;
                  }
                  else{
                    BoosterPlayer boosterPlayer = new BoosterPlayer(target);
                    boosterPlayer.giveBoosters(args[3], Integer.parseInt(args[4]));
                    sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() +
                            McBoosters.getInstance().getLangFile().getString("Messages.Admin.BoostersGiven").replace("%Player%", target.getName())
                                    .replace("%Amount%", args[4]).replace("%BoosterType%", args[3])));
                    return true;
                  }
                }
                else{
                  sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.NotAnInt")));
                  return true;
                }
              }
              else{
                sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.NotABooster")
                        .replace("%BoosterType%", args[3])));
                return true;
              }
            }
            else{
              sender.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + McBoosters.getInstance().getLangFile().getString("Messages.Util.PlayerNotLoggedIn")));
              return true;
            }
          }
        }
      }
    }
    return false;
  }
}
