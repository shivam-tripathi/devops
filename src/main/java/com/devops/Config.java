package com.devops;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Paths;
import java.util.HashMap;
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

  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SSM {
    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mongo {
      public String prod;
      public String game;
      public String dev;
    }

    public Mongo mongo;
  }

  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ECS {
    public List<String> services;
  }

  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Vpc {
    @Getter
    @Setter
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VpcConf {
      public String id;
      public HashMap<String, String> publicSubnets;
      public HashMap<String, String> privateSubnets;
    }
    public VpcConf prod;
    public VpcConf dev;
  }

  @Getter
  @Setter
  @ToString
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Iam {
    public String ec2MetricsInstanceProfileArn;
  }

  public Map<String, String> subnets;
  public Map<String, String> securityGroups;
  public CloudwatchConfig cloudwatch;
  public Infra infra;
  public String env;
  public SSM ssm;
  public ECS ecs;
  public Vpc vpc;
  public Iam iam;

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