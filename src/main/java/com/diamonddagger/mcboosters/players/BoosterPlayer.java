package com.diamonddagger.mcboosters.players;

import com.diamonddagger.mcboosters.McBoosters;
import com.diamonddagger.mcboosters.util.Methods;
import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.economy.BufferedPayment;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.eunoians.mcrpg.api.exceptions.McRPGPlayerNotFoundException;
import us.eunoians.mcrpg.players.McRPGPlayer;
import us.eunoians.mcrpg.players.PlayerManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class BoosterPlayer {

  @Getter
  private UUID uuid;

  @Getter
  @Setter
  private boolean debugMode = false;

  private Map<String, Integer> boosterAmounts = new HashMap<>();

  File playerFile;
  FileConfiguration config;

  public BoosterPlayer(Player p){
    this.uuid = p.getUniqueId();
    playerFile = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
    boolean exists = playerFile.exists();
    if(!exists){
      try{
        playerFile.createNewFile();
      } catch(IOException e){
        e.printStackTrace();
      }
    }
    config = YamlConfiguration.loadConfiguration(playerFile);
    if(exists){
      if(config.contains("Boosters")){
        for(String s : config.getConfigurationSection("Boosters").getKeys(false)){
          boosterAmounts.put(s.toLowerCase(), config.getInt("Boosters." + s));
        }
      }
      new BukkitRunnable(){
        @Override
        public void run(){
          boolean wasThanked = config.contains("CachedRewards") || config.contains("CachedCommands");
          //Deal with cached rewards
          AtomicInteger vanillaExp = new AtomicInteger(0);
          AtomicInteger mcrpgExp = new AtomicInteger(0);
          AtomicInteger commandsExecuted = new AtomicInteger(0);
          AtomicDouble money = new AtomicDouble(0);
          if(config.contains("CachedRewards")){
            //Bukkit.broadcastMessage("1o");
            if(McBoosters.getInstance().isMcrpgEnabled() && config.contains("CachedRewards.McRPGExp")){
              new BukkitRunnable(){
                @Override
                public void run(){
                  try{
                    //Bukkit.broadcastMessage("2o");
                    McRPGPlayer player = PlayerManager.getPlayer(uuid);
                    mcrpgExp.set(config.getInt("CachedRewards.McRPGExp"));
                    player.giveRedeemableExp(mcrpgExp.get());
                    player.saveData();
                    config.set("CachedRewards.McRPGExp", null);
                  } catch(McRPGPlayerNotFoundException e){
                    e.printStackTrace();
                  }
                }
              }.runTaskLater(McBoosters.getInstance(), 5 * 20);
            }
            if(config.contains("CachedRewards.VanillaExp")){
              //Bukkit.broadcastMessage("3o");
              vanillaExp.set(config.getInt("CachedRewards.VanillaExp"));
              p.giveExp(vanillaExp.get());
              config.set("CachedRewards.VanillaExp", null);
            }
            if(McBoosters.getInstance().isJobsEnabled() && config.contains("CachedRewards.JobsMoney")){
             // Bukkit.broadcastMessage("4o");
              money.set(config.getDouble("CachedRewards.JobsMoney"));
              Jobs.getEconomy().pay(new BufferedPayment(p, Map.of(CurrencyType.MONEY, money.get())));
              config.set("CachedRewards.JobsMoney", null);
            }
          }
          if(!config.contains("CachedRewards") || config.getConfigurationSection("CachedRewards").getKeys(false).size() == 0){
            //Bukkit.broadcastMessage("5o");
            config.set("CachedRewards", null);
          }
          if(config.contains("CachedCommands")){
            //Bukkit.broadcastMessage("6o");
            for(String s : config.getConfigurationSection("CachedCommands").getKeys(false)){
              String key = "CachedCommands." + s;
             // Bukkit.broadcastMessage("7o:" + config.getString(key));
              Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString(key).replace("%Player%", p.getName()));
              commandsExecuted.incrementAndGet();
            }
            config.set("CachedCommands", null);
          }
          new BukkitRunnable() {
            @Override
            public void run(){
              if(wasThanked){
                String key = "Messages.Boosters.OfflineThank.";
                FileConfiguration langFile = McBoosters.getInstance().getLangFile();
                String offlineThankedMessage = langFile.getString(key + "Message");
                String thanked = "";
                boolean prev = false;
                if(vanillaExp.get() > 0){
                  thanked += langFile.getString(key + "VanillaExp").replace("%Amount%", Integer.toString(vanillaExp.get()));
                  prev = true;
                }
                if(mcrpgExp.get() > 0){
                  if(prev){
                    thanked += ", ";
                  }
                  thanked += langFile.getString(key + "McRPGExp").replace("%Amount%", Integer.toString(mcrpgExp.get()));
                  prev = true;
                }
                if(money.get() > 0){
                  if(prev){
                    thanked += ", ";
                  }
                  thanked += langFile.getString(key + "Money").replace("%Amount%", Double.toString(money.get()));
                  prev = true;
                }
                if(commandsExecuted.get() > 0){
                  if(prev){
                    thanked += ", ";
                  }
                  thanked += langFile.getString(key + "Commands").replace("%Amount%", Integer.toString(commandsExecuted.get()));
                }
                if(!thanked.equalsIgnoreCase("")){
                  offlineThankedMessage = offlineThankedMessage.replace("%Rewards%", thanked);
                  p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + offlineThankedMessage));
                }
              }

              try{
                config.save(playerFile);
              } catch(IOException e){
                e.printStackTrace();
              }
            }
          }.runTaskLater(McBoosters.getInstance(), 6 * 20);

        }
      }.runTaskLater(McBoosters.getInstance(), 600);
    }
  }

  public BoosterPlayer(OfflinePlayer p){
    this.uuid = p.getUniqueId();
    playerFile = new File(McBoosters.getInstance().getDataFolder(), File.separator + "playerdata" + File.separator + uuid.toString() + ".yml");
    boolean exists = playerFile.exists();
    if(!exists){
      try{
        playerFile.createNewFile();
      } catch(IOException e){
        e.printStackTrace();
      }
    }
    config = YamlConfiguration.loadConfiguration(playerFile);
    if(exists){
      if(config.contains("Boosters")){
        for(String s : config.getConfigurationSection("Boosters").getKeys(false)){
          boosterAmounts.put(s.toLowerCase(), config.getInt("Boosters." + s));
        }
      }
    }
  }

  public void flushOfflineThanks(){
    Player p = getPlayer();
    config = YamlConfiguration.loadConfiguration(playerFile);
    if(config.contains("Boosters")){
      for(String s : config.getConfigurationSection("Boosters").getKeys(false)){
        boosterAmounts.put(s.toLowerCase(), config.getInt("Boosters." + s));
      }
    }
    new BukkitRunnable(){
      @Override
      public void run(){
        boolean wasThanked = config.contains("CachedRewards") || config.contains("CachedCommands");
        //Deal with cached rewards
        AtomicInteger vanillaExp = new AtomicInteger(0);
        AtomicInteger mcrpgExp = new AtomicInteger(0);
        AtomicInteger commandsExecuted = new AtomicInteger(0);
        AtomicDouble money = new AtomicDouble(0);
        if(config.contains("CachedRewards")){
          if(McBoosters.getInstance().isMcrpgEnabled() && config.contains("CachedRewards.McRPGExp")){
            new BukkitRunnable(){
              @Override
              public void run(){
                try{
                 // Bukkit.broadcastMessage("1f");
                  McRPGPlayer player = PlayerManager.getPlayer(uuid);
                  mcrpgExp.set(config.getInt("CachedRewards.McRPGExp"));
                  player.giveRedeemableExp(mcrpgExp.get());
                  player.saveData();
                  config.set("CachedRewards.McRPGExp", null);
                } catch(McRPGPlayerNotFoundException e){
                  e.printStackTrace();
                }
              }
            }.runTaskLater(McBoosters.getInstance(), 5 * 20);
          }
          if(config.contains("CachedRewards.VanillaExp")){
            //Bukkit.broadcastMessage("2f");
            vanillaExp.set(config.getInt("CachedRewards.VanillaExp"));
            p.giveExp(vanillaExp.get());
            config.set("CachedRewards.VanillaExp", null);
          }
          if(McBoosters.getInstance().isJobsEnabled() && config.contains("CachedRewards.JobsMoney")){
          //  Bukkit.broadcastMessage("3f");
            money.set(config.getDouble("CachedRewards.JobsMoney"));
            Jobs.getEconomy().pay(new BufferedPayment(p,  Map.of(CurrencyType.MONEY, money.get())));
            config.set("CachedRewards.JobsMoney", null);
          }
        }
        if(config.contains("CachedCommands")){
          for(String s : config.getConfigurationSection("CachedCommands").getKeys(false)){
           // Bukkit.broadcastMessage("Debugg 1: " + s);
            String key = "CachedCommands." + s;
           // Bukkit.broadcastMessage("Debugg 2: " + config.contains(key) + " " + config.getString(key));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString(key).replace("%Player%", p.getName()));
            commandsExecuted.incrementAndGet();
          }
          config.set("CachedCommands", null);
        }
        new BukkitRunnable() {
          @Override
          public void run(){
            if(wasThanked){
              String key = "Messages.Boosters.OfflineThank.";
              FileConfiguration langFile = McBoosters.getInstance().getLangFile();
              String offlineThankedMessage = langFile.getString(key + "Message");
              String thanked = "";
              boolean prev = false;
              if(vanillaExp.get() > 0){
                thanked += langFile.getString(key + "VanillaExp").replace("%Amount%", Integer.toString(vanillaExp.get()));
                prev = true;
              }
              if(mcrpgExp.get() > 0){
                if(prev){
                  thanked += ", ";
                }
                thanked += langFile.getString(key + "McRPGExp").replace("%Amount%", Integer.toString(mcrpgExp.get()));
                prev = true;
              }
              if(money.get() > 0){
                if(prev){
                  thanked += ", ";
                }
                thanked += langFile.getString(key + "Money").replace("%Amount%", Double.toString(money.get()));
                prev = true;
              }
              if(commandsExecuted.get() > 0){
                if(prev){
                  thanked += ", ";
                }
                thanked += langFile.getString(key + "Commands").replace("%Amount%", Integer.toString(commandsExecuted.get()));
              }
              if(!thanked.equalsIgnoreCase("")){
                offlineThankedMessage = offlineThankedMessage.replace("%Rewards%", thanked);
                p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + offlineThankedMessage));
              }
            }
            try{
              config.save(playerFile);
            } catch(IOException e){
              e.printStackTrace();
            }
          }
        }.runTaskLater(McBoosters.getInstance(), 6* 20);
        /*if(wasThanked){
          String key = "Messages.Boosters.OfflineThank.";
          FileConfiguration langFile = McBoosters.getInstance().getLangFile();
          String offlineThankedMessage = langFile.getString(key + "Message");
          String thanked = "";
          boolean prev = false;
          if(vanillaExp.get() > 0){
            thanked += langFile.getString(key + "VanillaExp").replace("%Amount%", Integer.toString(vanillaExp.get()));
            prev = true;
          }
          if(mcrpgExp.get() > 0){
            if(prev){
              thanked += ", ";
            }
            thanked += langFile.getString(key + "McRPGExp").replace("%Amount%", Integer.toString(mcrpgExp.get()));
            prev = true;
          }
          if(money > 0){
            if(prev){
              thanked += ", ";
            }
            thanked += langFile.getString(key + "Money").replace("%Amount%", Double.toString(money));
            prev = true;
          }
          if(commandsExecuted.get() > 0){
            if(prev){
              thanked += ", ";
            }
            thanked += langFile.getString(key + "Commands").replace("%Amount%", Integer.toString(commandsExecuted.get()));
          }
          offlineThankedMessage = offlineThankedMessage.replace("%Rewards%", thanked);
          if(!thanked.equalsIgnoreCase("")){
            p.sendMessage(Methods.color(McBoosters.getInstance().getPluginPrefix() + offlineThankedMessage));
          }
        }*/

        try{
          config.save(playerFile);
        } catch(IOException e){
          e.printStackTrace();
        }
      }
    }.runTaskLater(McBoosters.getInstance(), 600);
  }

  public Player getPlayer(){
    return Bukkit.getPlayer(uuid);
  }

  public boolean isOnline(){
    return Bukkit.getOfflinePlayer(uuid).isOnline();
  }

  public void giveBoosters(String boosterName, int amount){
    if(boosterAmounts.containsKey(boosterName)){
      boosterAmounts.replace(boosterName, boosterAmounts.get(boosterName) + amount);
    }
    else{
      boosterAmounts.put(boosterName, amount);
    }
    save();
  }

  public void setBoosters(String boosterName, int amount){
    if(boosterAmounts.containsKey(boosterName)){
      boosterAmounts.replace(boosterName, amount);
    }
    else{
      boosterAmounts.put(boosterName, amount);
    }
    save();
  }

  public boolean hasBoosters(String boosterName){
    return boosterAmounts.containsKey(boosterName);
  }

  public int getBoosterAmount(String boosterName){
    return boosterAmounts.getOrDefault(boosterName, 0);
  }

  //PRE: Validate that they have enough boosters for decrementing
  public void decrementBoosterAmount(String boosterName, int amount){
    int newAmount = boosterAmounts.get(boosterName) - amount;
    if(newAmount <= 0){
      boosterAmounts.remove(boosterName);
    }
    else{
      boosterAmounts.replace(boosterName, newAmount);
    }
  }

  public void save(){
    config = YamlConfiguration.loadConfiguration(playerFile);
    config.set("Boosters", null);
    for(String s : boosterAmounts.keySet()){
      config.set("Boosters." + s, boosterAmounts.get(s));
    }
    try{
      config.save(playerFile);
    } catch(IOException e){
      e.printStackTrace();
    }
  }
}