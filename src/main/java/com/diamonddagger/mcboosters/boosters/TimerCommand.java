package com.diamonddagger.mcboosters.boosters;

public class TimerCommand{
  
  private String command;
  private int multiplier;
  
  public TimerCommand(String command, int multiplier){
    this.command = command;
    this.multiplier = multiplier;
  }
  
  public String getCommand(){
    return command;
  }
  
  public int getMultiplier(){
    return multiplier;
  }
  
}
