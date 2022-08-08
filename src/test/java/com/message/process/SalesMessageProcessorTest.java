package com.message.process;


import com.message.process.model.AdjustmentNotification;
import com.message.process.model.AdjustmentOperations;
import com.message.process.model.BasicNotification;
import com.message.process.model.OccurrencesNotification;
import com.message.process.service.SalesMessageProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


public class SalesMessageProcessorTest {

  private static final String TEST_PRODUCT = "apple";
  private static final BigDecimal TEST_VALUE = new BigDecimal(50);
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private SalesMessageProcessor salesMessageProcessor;

  @BeforeEach
  void setup() {
    System.setOut(new PrintStream(outContent));
    salesMessageProcessor = new SalesMessageProcessor();
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  @Test
  void whenReceivingBasicNotification_thenSaveNewRecordInStorageWithProductAndValue() {
    BasicNotification notification = new BasicNotification(TEST_VALUE, TEST_PRODUCT);
    salesMessageProcessor.messageRouter(notification);
    assertTrue(salesMessageProcessor.getDataStorage().containsKey(TEST_PRODUCT));
    assertFalse(salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).isEmpty());
    assertEquals(TEST_VALUE, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(0).getRevenue());

  }

  @Test
  void whenReceivingOccurrencesNotification_thenSaveNumberOfOccurrencesAsNewRecordsInStorageWithProductAndValue() {
    BigDecimal totalValue = new BigDecimal(250);
    OccurrencesNotification notification = new OccurrencesNotification(TEST_VALUE, TEST_PRODUCT, 5);
    salesMessageProcessor.messageRouter(notification);
    assertTrue(salesMessageProcessor.getDataStorage().containsKey(TEST_PRODUCT));
    assertEquals(5, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).size());
    assertEquals(TEST_VALUE, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(0).getRevenue());
    assertEquals(TEST_VALUE, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(4).getRevenue());
    assertEquals(totalValue, salesMessageProcessor.getTotalSalesPerType().get(TEST_PRODUCT));
  }

  @Test
  void whenReceivingOccurrencesNotificationForExistProduct_theAddRecordsInStorageWithProductAndValue() {
    BigDecimal totalValue = new BigDecimal(300);
    BasicNotification basicNotification = new BasicNotification(TEST_VALUE, TEST_PRODUCT);
    OccurrencesNotification occurrenceNotification = new OccurrencesNotification(TEST_VALUE, TEST_PRODUCT, 5);
    salesMessageProcessor.messageRouter(basicNotification);
    salesMessageProcessor.messageRouter(occurrenceNotification);
    assertTrue(salesMessageProcessor.getDataStorage().containsKey(TEST_PRODUCT));
    assertEquals(6, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).size());
    assertEquals(TEST_VALUE, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(1).getRevenue());
    assertEquals(TEST_VALUE, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(5).getRevenue());
    assertEquals(totalValue, salesMessageProcessor.getTotalSalesPerType().get(TEST_PRODUCT));
  }

  @Test
  void whenReceivingAdditionAdjustmentNotification_thenApplyAdjustmentsAndSaveItToAdjustmentsStorage() {
    BigDecimal valueAfterAdjustment = new BigDecimal(100);
    AdjustmentNotification notification = new AdjustmentNotification(TEST_VALUE, TEST_PRODUCT, AdjustmentOperations.ADD);
    sendBasicNotifications(5);
    salesMessageProcessor.messageRouter(notification);
    assertEquals(valueAfterAdjustment, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(0).getRevenue());
    assertFalse(salesMessageProcessor.getAdjustmentEventStorage().isEmpty());
    assertEquals(1, salesMessageProcessor.getAdjustmentEventStorage().get(TEST_PRODUCT).getAdditions());
  }

  @Test
  void whenReceivingSubtractionAdjustmentNotification_thenApplyAdjustmentsAndSaveItToAdjustmentsStorage() {
    BigDecimal valueAfterAdjustment = new BigDecimal(30);
    AdjustmentNotification notification = new AdjustmentNotification(new BigDecimal(20), TEST_PRODUCT, AdjustmentOperations.SUBTRACT);
    sendBasicNotifications(5);
    salesMessageProcessor.messageRouter(notification);
    assertEquals(valueAfterAdjustment, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(0).getRevenue());
    assertFalse(salesMessageProcessor.getAdjustmentEventStorage().isEmpty());
    assertEquals(1, salesMessageProcessor.getAdjustmentEventStorage().get(TEST_PRODUCT).getSubtractions());
  }

  @Test
  void whenReceivingMultiplicationAdjustmentNotification_thenApplyAdjustmentsAndSaveItToAdjustmentsStorage() {
    BigDecimal valueAfterAdjustment = new BigDecimal(500);
    AdjustmentNotification notification = new AdjustmentNotification(new BigDecimal(10), TEST_PRODUCT, AdjustmentOperations.MULTIPLY);
    sendBasicNotifications(5);
    salesMessageProcessor.messageRouter(notification);
    assertEquals(valueAfterAdjustment, salesMessageProcessor.getDataStorage().get(TEST_PRODUCT).get(0).getRevenue());
    assertFalse(salesMessageProcessor.getAdjustmentEventStorage().isEmpty());
    assertEquals(1, salesMessageProcessor.getAdjustmentEventStorage().get(TEST_PRODUCT).getMultiplications());
  }

  @Test
  void whenSending10BasicNotifications_thenWeShouldPrintRightReport() {
    sendBasicNotifications(10);
    assertEquals(outContent.toString(), "Number of sales for product apple are 10 and Total value is 500\n");
  }

  @Test
  void whenSending50Notifications_thenWeShouldPrintAdjustmentReport() {
    sendBasicNotifications(47);
    AdjustmentNotification notification1 = new AdjustmentNotification(TEST_VALUE, TEST_PRODUCT, AdjustmentOperations.ADD);
    AdjustmentNotification notification2 = new AdjustmentNotification(TEST_VALUE, TEST_PRODUCT, AdjustmentOperations.SUBTRACT);
    AdjustmentNotification notification3 = new AdjustmentNotification(TEST_VALUE, TEST_PRODUCT, AdjustmentOperations.MULTIPLY);
    salesMessageProcessor.messageRouter(notification1);
    salesMessageProcessor.messageRouter(notification2);
    salesMessageProcessor.messageRouter(notification3);
    assertTrue(outContent.toString().contains("Logging report for adjustments that have been made to each sale type"));
    assertTrue(outContent.toString().contains("Product apple Adjustment operations are {additions=1, subtractions=1, multiplications=1}"));
  }


  private void sendBasicNotifications(int counter) {
    BasicNotification notification = new BasicNotification(TEST_VALUE, TEST_PRODUCT);
    IntStream.range(1, counter + 1).forEach(t -> salesMessageProcessor.messageRouter(notification));
  }


}
