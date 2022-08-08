package com.message.process.model;

public class AdjustmentTracking {

  private int additions;
  private int subtractions;
  private int multiplications;

  public void incrementAddition() {
    additions++;
  }

  public void incrementSubtraction() {
    subtractions++;
  }

  public void incrementMultiplication() {
    multiplications++;
  }

  public int getAdditions() {
    return additions;
  }

  public int getSubtractions() {
    return subtractions;
  }

  public int getMultiplications() {
    return multiplications;
  }

  @Override
  public String toString() {
    return "Adjustment operations are {" +
        "additions=" + additions +
        ", subtractions=" + subtractions +
        ", multiplications=" + multiplications +
        '}';
  }
}
