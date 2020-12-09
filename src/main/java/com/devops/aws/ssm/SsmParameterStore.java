package com.devops.aws.ssm;

import com.amazonaws.services.simplesystemsmanagement.model.*;

import java.util.ArrayList;
import java.util.List;

public class SsmParameterStore {
  public List<Parameter> getParametersByPath(String path) {
    GetParametersByPathRequest request = new GetParametersByPathRequest().withPath(path);
    String token;
    List<Parameter> parameters = new ArrayList<>();
    do {
      GetParametersByPathResult res = SsmHelper.getClient().getParametersByPath(request);
      token = res.getNextToken();
      request.setNextToken(token);
      System.out.println(res.getParameters());
      parameters.addAll(res.getParameters());
    } while (token != null);
    return parameters;
  }

  public Parameter getParameterByName(String name) {
    return SsmHelper.getClient().getParameter(new GetParameterRequest().withName(name)).getParameter();
  }

  public void putParameterByName(String name, String value, boolean overwrite) {
    PutParameterRequest request = new PutParameterRequest().withName(name).withValue(value).withOverwrite(overwrite);
    SsmHelper.getClient().putParameter(request);
  }
}
