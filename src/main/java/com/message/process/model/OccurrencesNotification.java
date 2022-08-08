package com.message.process.model;

import java.math.BigDecimal;

public class OccurrencesNotification extends BasicNotification {

  private int occurrences;

  public OccurrencesNotification(BigDecimal value, String product, int occurrences) {
    super(value, product);
    this.occurrences = occurrences;
  }

  public int getOccurrences() {
    return occurrences;
  }
}
