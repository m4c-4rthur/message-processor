package com.message.process.model;

import java.math.BigDecimal;

public class AdjustmentNotification extends BasicNotification {

  private AdjustmentOperations adjustmentOperation;

  public AdjustmentNotification(BigDecimal count, String type, AdjustmentOperations adjustmentOperation) {
    super(count, type);
    this.adjustmentOperation = adjustmentOperation;
  }

  public AdjustmentOperations getAdjustmentOperation() {
    return adjustmentOperation;
  }
}
