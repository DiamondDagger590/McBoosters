package com.diamonddagger.mcboosters;

import com.diamonddagger.mcboosters.announcer.Announcer;
import com.diamonddagger.mcboosters.boosters.BoosterManager;
import com.diamonddagger.mcboosters.commands.CommandPrompt;
import com.diamonddagger.mcboosters.commands.McBoosterStub;
import com.diamonddagger.mcboosters.discord.DiscordManager;
import com.diamonddagger.mcboosters.events.vanilla.*;
import com.diamonddagger.mcboosters.players.PlayerManager;
import com.diamonddagger.mcboosters.util.FileManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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
  private Announcer announcer;
  @Getter
  private boolean mcrpgEnabled = false;



  @Override
  public void onEnable(){
    instance = this;
    fileManager.setup(this);
    boosterManager = new BoosterManager();
    discordManager = new DiscordManager();
    playerManager = new PlayerManager();
    announcer = new Announcer();

    if(Bukkit.getPluginManager().isPluginEnabled("McRPG")){
      mcrpgEnabled = true;
      Bukkit.getPluginManager().registerEvents(new McRPGExpEvent(), this);
    }
    Bukkit.getPluginManager().registerEvents(new JobsEvents(), this);
    Bukkit.getPluginManager().registerEvents(new InvClick(), this);
    Bukkit.getPluginManager().registerEvents(new InvClose(), this);
    Bukkit.getPluginManager().registerEvents(new KillEvent(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerLogout(), this);
    getCommand("mcbooster").setExecutor(new McBoosterStub());
    Bukkit.getServer().getPluginCommand("mcbooster").setTabCompleter(new CommandPrompt());
  }

  @Override
  public void onDisable(){
    boosterManager.backup();
  }

  @Override
  public FileConfiguration getConfig(){
    return fileManager.getFile(FileManager.Files.CONFIG);
  }

  public FileConfiguration getLangFile() {
    return FileManager.Files.fromString(fileManager.getFile(FileManager.Files.CONFIG).getString("Configuration.LangFile")).getFile();
  }

  public String getPluginPrefix(){
    return getLangFile().getString("PluginPrefix");
  }
}
