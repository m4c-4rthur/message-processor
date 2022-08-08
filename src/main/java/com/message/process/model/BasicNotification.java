package com.message.process.model;

import java.math.BigDecimal;

public class BasicNotification {

  private BigDecimal value;
  private String product;

  public BasicNotification(BigDecimal value, String product) {
    this.value = value;
    this.product = product;
  }

  public BigDecimal getValue() {
    return value;
  }

  public String getProduct() {
    return product;
  }

}
