package com.message.process.service;

import com.message.process.model.AdjustmentNotification;
import com.message.process.model.BasicNotification;
import com.message.process.model.OccurrencesNotification;

public interface IMessageProcessor {

  void handleBasicMessage(BasicNotification basicNotification);

  void handleMessageWithOccurrences(OccurrencesNotification occurrencesNotification);

  void handleMessageWithAdjustments(AdjustmentNotification adjustmentNotification);

  void handleReporting();

  void messageRouter(BasicNotification notification);
}
