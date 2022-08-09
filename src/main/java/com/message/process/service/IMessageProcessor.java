package com.message.process.service;

public interface IMessageProcessor<T> {


  void handleReporting();

  void messageRouter(T notification);
}
