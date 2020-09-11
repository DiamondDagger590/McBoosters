package com.diamonddagger.mcboosters.boosters;

import com.diamonddagger.mcboosters.types.BoostType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoosterRewardWrapper{
  
  private UUID uuid;
  private Map<BoostType, Double> boostsFromTypes = new HashMap<>();
  
  public BoosterRewardWrapper(UUID uuid){
    this.uuid = uuid;
  }
  
  /**
   * Sets the amount of bonus exp obtained from the boost type.
   * If any value was previously stored, it is overridden
   * @param boostType The {@link BoostType} that caused the boost
   * @param amount The amount of extra exp the player has earned
   */
  public void setBoostFromType(BoostType boostType, double amount){
    boostsFromTypes.put(boostType, amount);
  }
  
  public void addBoostFromType(BoostType boostType, double amount){
    boostsFromTypes.put(boostType, boostsFromTypes.getOrDefault(boostType, 0d) + amount);
  }
  
  /**
   * Returns a string representation of the object. In general, the
   * {@code toString} method returns a string that
   * "textually represents" this object. The result should
   * be a concise but informative representation that is easy for a
   * person to read.
   * It is recommended that all subclasses override this method.
   * <p>
   * The {@code toString} method for class {@code Object}
   * returns a string consisting of the name of the class of which the
   * object is an instance, the at-sign character `{@code @}', and
   * the unsigned hexadecimal representation of the hash code of the
   * object. In other words, this method returns a string equal to the
   * value of:
   * <blockquote>
   * <pre>
   * getClass().getName() + '@' + Integer.toHexString(hashCode())
   * </pre></blockquote>
   *
   * @return a string representation of the object.
   */
  @Override
  public String toString(){
    StringBuilder builder = new StringBuilder();
    
    builder.append("[ ");
    
    for(BoostType boostType : boostsFromTypes.keySet()){
      builder.append("boostType: ").append(boostType.getName()).append(" Exp: ").append(boostsFromTypes.get(boostType)).append(" ");
    }
    
    builder.append("]");
    return builder.toString();
  }
}
