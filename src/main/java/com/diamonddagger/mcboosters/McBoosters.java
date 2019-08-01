package com.diamonddagger.mcboosters;

import com.diamonddagger.mcboosters.boosters.BoosterManager;
import com.diamonddagger.mcboosters.discord.DiscordManager;
import com.diamonddagger.mcboosters.players.PlayerManager;
import com.diamonddagger.mcboosters.util.FileManager;
import lombok.Getter;
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
  private boolean mcrpgEnabled;



  @Override
  public void onEnable(){
    instance = this;
    fileManager.setup(this);

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
