package com.github.princesslana.totwentytwo;

public interface Round {
  public void onMessage(User who, String msg);

  public boolean isDone(boolean checkTimeout);

  public Result getResult();
}
