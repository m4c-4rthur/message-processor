package com.message.process.service;

import com.message.process.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is handling the processing of sales notifications by saving them in a common storage
 * having a reporting requirements which involve checking the history of processed data, I have always aimed to minimize any costly operation O(n)
 * by having multiple memoization storages to avoid any full scan operations.
 * for the sake of simplicity I haven't used advanced topics (multithreading) or complex pattern s(for example the different message handlers was good candidate for factory pattern)
 * the test coverage for this class is 100%.
 */

public class SalesMessageProcessor implements IMessageProcessor {

  private Map<String, List<SalesRecord>> dataStorage = new HashMap<>();
  private Map<String, AdjustmentTracking> adjustmentEventStorage = new HashMap<>();
  private Map<String, BigDecimal> totalSalesPerType = new HashMap<>();
  private int notificationCounter;

  @Override
  public void handleBasicMessage(BasicNotification basicNotification) {
    if (dataStorage.containsKey(basicNotification.getProduct())) {
      dataStorage.get(basicNotification.getProduct()).add(new SalesRecord(basicNotification.getValue()));
      totalSalesPerType.merge(basicNotification.getProduct(), basicNotification.getValue(), BigDecimal::add);
    } else {
      dataStorage.put(basicNotification.getProduct(), Stream.of(new SalesRecord(basicNotification.getValue()))
          .collect(Collectors.toList()));
      totalSalesPerType.put(basicNotification.getProduct(), basicNotification.getValue());
    }
  }

  @Override
  public void handleMessageWithOccurrences(OccurrencesNotification notification) {
    if (dataStorage.containsKey(notification.getProduct())) {
      dataStorage.get(notification.getProduct()).addAll(Collections.nCopies(notification.getOccurrences(), new SalesRecord(notification.getValue())));
      totalSalesPerType.merge(notification.getProduct(), notification.getValue().multiply(BigDecimal.valueOf(notification.getOccurrences())), BigDecimal::add);
    } else {
      dataStorage.put(notification.getProduct(), Collections.nCopies(notification.getOccurrences(), new SalesRecord(notification.getValue())));
      totalSalesPerType.put(notification.getProduct(), notification.getValue().multiply(BigDecimal.valueOf(notification.getOccurrences())));
    }
  }

  @Override
  public void handleMessageWithAdjustments(AdjustmentNotification adjustmentNotification) {
    if (!adjustmentEventStorage.containsKey(adjustmentNotification.getProduct()))
      adjustmentEventStorage.put(adjustmentNotification.getProduct(), new AdjustmentTracking());
    performAdjustmentOperation(adjustmentNotification);
  }

  private void performAdjustmentOperation(AdjustmentNotification adjustmentNotification) {
    switch (adjustmentNotification.getAdjustmentOperation()) {
      case ADD:
        dataStorage.get(adjustmentNotification.getProduct()).forEach(t -> {
          t.incrementValue(adjustmentNotification.getValue());
          totalSalesPerType.merge(adjustmentNotification.getProduct(), adjustmentNotification.getValue(), BigDecimal::add);
        });
        adjustmentEventStorage.get(adjustmentNotification.getProduct()).incrementAddition();
        break;
      case SUBTRACT:
        dataStorage.get(adjustmentNotification.getProduct()).forEach(t -> {
          t.subtractValue(adjustmentNotification.getValue());
          totalSalesPerType.merge(adjustmentNotification.getProduct(), adjustmentNotification.getValue(), BigDecimal::subtract);
        });
        adjustmentEventStorage.get(adjustmentNotification.getProduct()).incrementSubtraction();
        break;
      case MULTIPLY:
        dataStorage.get(adjustmentNotification.getProduct()).forEach(t -> {
          BigDecimal currentVal = t.getRevenue();
          t.multiplyValue(adjustmentNotification.getValue());
          totalSalesPerType.merge(adjustmentNotification.getProduct(), t.getRevenue().subtract(currentVal), BigDecimal::multiply);
        });
        adjustmentEventStorage.get(adjustmentNotification.getProduct()).incrementMultiplication();
        break;
      default:
        break;
    }
  }


  @Override
  public void handleReporting() {
    if (notificationCounter % 10 == 0) {
      dataStorage.forEach((k, v) -> System.out.printf("Number of sales for product %s are %s and Total value is %s%n", k, v.size(), totalSalesPerType.get(k)));
    }
    if (notificationCounter % 50 == 0) {
      System.out.println("Logging report for adjustments that have been made to each sale type");
      adjustmentEventStorage.forEach((k, v) -> System.out.printf("Product %s %s%n", k, v));
    }
  }

  @Override
  public void messageRouter(BasicNotification notification) {
    notificationCounter++;
    if (notification instanceof OccurrencesNotification)
      this.handleMessageWithOccurrences((OccurrencesNotification) notification);
    else if (notification instanceof AdjustmentNotification)
      this.handleMessageWithAdjustments((AdjustmentNotification) notification);
    else
      this.handleBasicMessage(notification);
    handleReporting();
  }


  public Map<String, List<SalesRecord>> getDataStorage() {
    return dataStorage;
  }

  public Map<String, AdjustmentTracking> getAdjustmentEventStorage() {
    return adjustmentEventStorage;
  }

  public Map<String, BigDecimal> getTotalSalesPerType() {
    return totalSalesPerType;
  }
}
