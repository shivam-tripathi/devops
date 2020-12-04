package com.devops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CloudwatchConfig {
    List<String> okActions;
    List<String> alarmActions;
  }
  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class InfraRabbit {
    List<String> queues;
  }
  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Infra {
    InfraRabbit rabbit;
  }
  private Map<String, String> subnets;
  private Map<String, String> securityGroups;
  private CloudwatchConfig cloudwatch;
  private Infra infra;
  private String env;

  private static Config config = null;

  static {
    try {
      Map<String, String> env = System.getenv();
      ObjectMapper mapper = new ObjectMapper();
      // config = mapper.readValue(env.get("JAVA_CONFIG"), Config.class);
      config = mapper.readValue(Paths.get("config.json").toFile(), Config.class);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error Occurred" + e.getMessage() + e);
      System.exit(1);
    }
  }

  public static Config getConfig() {
    return config;
  }
}