package com.message.process;


import com.message.process.service.FileBasedNotificationOrganizer;

import java.io.IOException;

public class MessageProcessingApp {


  public static void main(String[] args) throws IOException {
    FileBasedNotificationOrganizer organizer = new FileBasedNotificationOrganizer();
    //organizer.generateDataFile(1000);
    organizer.sendDataToProcessor();
  }
}
