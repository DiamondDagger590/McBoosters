package com.diamonddagger.mcboosters;

import com.diamonddagger.mcboosters.boosters.BoosterManager;
import com.diamonddagger.mcboosters.commands.McBoosterStub;
import com.diamonddagger.mcboosters.discord.DiscordManager;
import com.diamonddagger.mcboosters.events.vanilla.KillEvent;
import com.diamonddagger.mcboosters.events.vanilla.McRPGExpEvent;
import com.diamonddagger.mcboosters.events.vanilla.PlayerLogin;
import com.diamonddagger.mcboosters.events.vanilla.PlayerLogout;
import com.diamonddagger.mcboosters.players.PlayerManager;
import com.diamonddagger.mcboosters.util.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import us.eunoians.mcrpg.events.vanilla.BreakEvent;

public final class McBoosters extends JavaPlugin {

  @Getter
  private static McBoosters instance;
  @Getter
  private FileManager fileManager = new FileManager();
  @Getter
  private BoosterManager boosterManager;
  @Getter
  private DiscordManager discordManager;
  @Getter
  private PlayerManager playerManager;
  @Getter
  private boolean mcrpgEnabled;



  @Override
  public void onEnable(){
    instance = this;
    fileManager.setup(this);
    boosterManager = new BoosterManager();
    discordManager = new DiscordManager();
    playerManager = new PlayerManager();
    if(Bukkit.getPluginManager().isPluginEnabled("McRPG")){
      mcrpgEnabled = true;
      Bukkit.getPluginManager().registerEvents(new McRPGExpEvent(), this);
    }
    Bukkit.getPluginManager().registerEvents(new KillEvent(), this);
    Bukkit.getPluginManager().registerEvents(new BreakEvent(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerLogout(), this);
    getCommand("mcbooster").setExecutor(new McBoosterStub());
  }

  @Override
  public void onDisable(){
    // Plugin shutdown logic
  }

  public FileConfiguration getLangFile(){
    return null;
  }

  public String getPluginPrefix(){
    return getLangFile().getString("PluginPrefix");
  }
}
