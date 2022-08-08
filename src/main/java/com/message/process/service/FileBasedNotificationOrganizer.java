package com.message.process.service;

import com.message.process.model.AdjustmentNotification;
import com.message.process.model.AdjustmentOperations;
import com.message.process.model.BasicNotification;
import com.message.process.model.OccurrencesNotification;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;


public class FileBasedNotificationOrganizer implements INotificationOrganizer {

  @Override
  public void sendDataToProcessor() {
    Path filePath = Paths.get("./test.txt");
    IMessageProcessor salesMessageProcessor = new SalesMessageProcessor();
    try (Stream<String> stream = Files.lines(filePath)) {
      stream.forEach(t -> {
        String[] value = t.split(",");
        switch (value[0]) {
          case "1":
            salesMessageProcessor.messageRouter(new BasicNotification(BigDecimal.valueOf(Integer.parseInt(value[2])), value[1]));
            break;
          case "2":
            salesMessageProcessor.messageRouter(new OccurrencesNotification(BigDecimal.valueOf(Integer.parseInt(value[1])), value[2], Integer.parseInt(value[3])));
            break;
          case "3":
            salesMessageProcessor.messageRouter(new AdjustmentNotification(BigDecimal.valueOf(Integer.parseInt(value[2])), value[3], AdjustmentOperations.valueOf(value[1])));
            break;
        }
      });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void generateDataFile(int count) throws IOException {
    String[] products = new String[]{"apple", "banana", "tea"};
    String[] operations = new String[]{"ADD", "SUBTRACT", "MULTIPLY"};
    Path filePath = Paths.get("./test-data.txt");
    for (int i = 0; i < count; i++) {
      int type = i % 3 + 1;
      String row = "";
      switch (type) {
        case 1:
          row = String.format("%s,%s,%s\n", 1, products[randomNumberBetween0And2()], randomNumberBetween10And100());
          break;
        case 2:
          row = String.format("%s,%s,%s,%s\n", 2, randomNumberBetween10And100(), products[randomNumberBetween0And2()], randomNumberBetween10And100());
          break;
        case 3:
          row = String.format("%s,%s,%s,%s\n", 3, operations[randomNumberBetween0And2()], randomNumberBetween10And100(), products[randomNumberBetween0And2()]);
          break;
      }
      Files.writeString(filePath, row, StandardOpenOption.APPEND);
    }


  }

  int randomNumberBetween0And2() {
    return (int) (Math.random() * 100) % 3;
  }

  int randomNumberBetween10And100() {
    return (int) ((Math.random() * (100 - 10)) + 10);
  }

}
