package com.diamonddagger.mcboosters.discord;

import github.scarsz.discordsrv.util.DiscordUtil;

public class DiscordSRV {

  public static void sendMessage(String channel, String message) {
    DiscordUtil.sendMessage(DiscordUtil.getTextChannelById(channel), message);
  }

}
