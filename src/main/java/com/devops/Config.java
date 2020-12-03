package com.devops;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Config {
  private String env;
  private Map<String, String> subnets;
  private Map<String, String> securityGroups;

  private static Config config = null;

  static {
    try {
      Map<String, String> env = System.getenv();
      ObjectMapper mapper = new ObjectMapper();
      config = mapper.readValue(env.get("JAVA_CONFIG"), Config.class);
    } catch (Exception e) {
      System.out.println("Error Occurred" + e.getMessage() + e);
      System.exit(1);
    }
  }

  public String getEnv() {
    return config.env;
  }

  public Map<String, String> getSubnets() {
    return config.subnets;
  }

  public Map<String, String> getSecurityGroups() {
    return config.securityGroups;
  }

  public static Config getConfig() {
    return config;
  }
}