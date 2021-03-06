package com.diamonddagger.mcboosters.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoosterEvent extends Event implements Cancellable {

  private static final HandlerList handlers = new HandlerList();

  protected boolean isCancelled = false;

  @Override
  public boolean isCancelled(){
    return isCancelled;
  }

  @Override
  public void setCancelled(boolean cancelled){
    isCancelled = cancelled;
  }

  @Override
  public HandlerList getHandlers(){
    return handlers;
  }
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
