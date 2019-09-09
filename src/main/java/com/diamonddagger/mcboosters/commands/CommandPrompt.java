package com.diamonddagger.mcboosters.commands;

import com.diamonddagger.mcboosters.McBoosters;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandPrompt implements TabCompleter {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String commandLable, String[] args){

    List<String> completions = new ArrayList<>();
    Player p = (Player) sender;
    if(args.length == 1){
      completions.add("help");
      if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.reload")) completions.add("reload");
      if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.admin.*") || p.hasPermission("mcbooster.admin.give") || p.hasPermission("mcbooster.admin.cancel"))
        completions.add("admin");
      return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
    }
    else if(args.length == 2){
      switch(args[0].toLowerCase()){
        case "help":
        case "reload":
        case "admin":
          if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.admin.*") || p.hasPermission("mcbooster.admin.give"))
            completions.add("give");
          if(p.hasPermission("mcbooster.*") || p.hasPermission("mcbooster.admin.*") || p.hasPermission("mcbooster.admin.cancel"))
            completions.add("cancel");
          break;
      }
      return StringUtil.copyPartialMatches(args[1], completions, new ArrayList<>());
    }
    else if(args.length == 3){
      switch(args[0].toLowerCase()){
        case "admin":
          switch(args[1].toLowerCase()){
            case "give":
              for(Player player : Bukkit.getOnlinePlayers()){
                completions.add(player.getName());
              }
              break;
            case "cancel":
              completions.addAll(McBoosters.getInstance().getBoosterManager().getAllActiveBoosterTypes());
              break;
          }
          break;
      }
      return StringUtil.copyPartialMatches(args[2], completions, new ArrayList<>());
    }
    else if(args.length == 4){
      switch(args[0].toLowerCase()){
        case "admin":
          switch(args[1].toLowerCase()){
            case "give":
              completions.addAll(McBoosters.getInstance().getBoosterManager().getAllBoosterTypes());
              break;
          }
          break;
      }
      return StringUtil.copyPartialMatches(args[3], completions, new ArrayList<>());
    }
    else if(args.length == 5){
      switch(args[0].toLowerCase()){
        case "admin":
          switch(args[1].toLowerCase()){
            case "give":
              completions.add("1");
              break;
          }
          break;
      }
      return StringUtil.copyPartialMatches(args[4], completions, new ArrayList<>());
    }
    return null;
  }
}