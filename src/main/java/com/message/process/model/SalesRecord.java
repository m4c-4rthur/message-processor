package com.message.process.model;

import java.math.BigDecimal;

public class SalesRecord {

  private BigDecimal revenue;

  public SalesRecord(BigDecimal revenue) {
    this.revenue = revenue;
  }


  public BigDecimal getRevenue() {
    return revenue;
  }

  public void incrementValue(BigDecimal value) {
    this.revenue = this.revenue.add(value);
  }

  public void subtractValue(BigDecimal value) {
    this.revenue = this.revenue.subtract(value);
  }

  public void multiplyValue(BigDecimal value) {
    this.revenue = this.revenue.multiply(value);
  }


}
