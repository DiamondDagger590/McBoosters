package com.diamonddagger.mcboosters.util;

import com.diamonddagger.mcboosters.guis.GUIItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Methods {

  /**
   * @param s String to test
   * @return true if the string is an int or false if not
   */
  public static boolean isInt(String s){
    try{
      Integer.parseInt(s);
    } catch(NumberFormatException nfe){
      return false;
    }
    return true;
  }

  /**
   * @param s String to test
   * @return true if the string is a long or false if not
   */
  public static boolean isLong(String s){
    try{
      Long.parseLong(s);
    } catch(NumberFormatException nfe){
      return false;
    }
    return true;
  }

  /**
   * @param msg String to colour
   * @return The coloured string
   */
  public static String color(String msg){
    return ChatColor.translateAlternateColorCodes('&', msg);
  }

  /**
   * @param minutes The number of minutes to convert
   * @return The ticks equal to minute amount
   */
  public static int convertMinToTicks(int minutes){
    int ticks = minutes * 1200;
    return ticks;
  }

  /**
   * @param uuid UUID to test
   * @return true if the player has logged in before or false if they have not
   */
  public static boolean hasPlayerLoggedInBefore(UUID uuid){
    OfflinePlayer targ = Bukkit.getOfflinePlayer(uuid);
    if(!(targ.isOnline() || targ.hasPlayedBefore())){
      return false;
    }
    else{
      return true;
    }
  }

  @SuppressWarnings("deprecation")
  public static boolean hasPlayerLoggedInBefore(String playerName){
    OfflinePlayer targ = Bukkit.getOfflinePlayer(playerName);
    if(!(targ.isOnline() || targ.hasPlayedBefore())){
      return false;
    }
    else{
      return true;
    }
  }

  /**
   * @param lore The list of strings to colour
   * @return The list of coloured strings
   */
  public static List<String> colorLore(List<String> lore){
    for(int i = 0; i < lore.size(); i++){
      String s = lore.get(i);
      lore.set(i, Methods.color(s));
    }
    return lore;
  }

  /**
   * @param inv    The inventory to fill
   * @param filler The item stack to fill air slots with
   * @param items  The array list of GUIItems to put in the inventory
   * @return
   */
  public static Inventory fillInventory(Inventory inv, ItemStack filler, ArrayList<GUIItem> items){
    for(GUIItem item : items){
      if(item.getItemStack() == null){
        continue;
      }
      inv.setItem(item.getSlot(), item.getItemStack());
    }
    for(int i = 0; i < inv.getSize(); i++){
      ItemStack testItem = inv.getItem(i);
      if(testItem == null && filler != null){
        inv.setItem(i, filler);
      }
    }
    return inv;
  }

  /**
   * @return Current time in millis
   */
  public static long getCurrentTimeInMillis(){
    Calendar cal = Calendar.getInstance();
    return cal.getTimeInMillis();
  }

  public static String convertMilisRemainder(String s, long time){
    if(time <= 0){
      return color(s.replace("%Hour%", Long.toString(0)).replace("%Minute%", Long.toString(0)).replace("%Second%", Long.toString(0)));

    }
    Calendar cal = Calendar.getInstance();
    time = time - cal.getTimeInMillis();
    long seconds, minutes, hours;
    seconds = time / 1000;
    minutes = seconds / 60;
    seconds = seconds % 60;
    hours = minutes / 60;
    minutes = minutes % 60;
    return color(s.replace("%Hour%", Long.toString(hours)).replace("%Minute%", Long.toString(minutes)).replace("%Second%", Long.toString(seconds)));
  }
}
