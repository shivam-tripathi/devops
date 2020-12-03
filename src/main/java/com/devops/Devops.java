package com.devops;

import com.devops.services.ECSActions;

public class Devops {
  public static void main(String[] args) {
    try {
      new ECSActions().validateTasks(1606799363021L, new String[]{"notif", "main", "services", "games2", "prod-services-v2"});
//      new ECSActions().forceRestartServices(Arrays.asList("notif", "main", "services", "games2", "prod-services-v2"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
