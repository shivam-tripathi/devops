package com.devops.utils.batch;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@ToString
@Getter
public class BatchExecuteResult<R> {
  List<Boolean> success;
  List<Exception> exceptions;
  List<R> results;
  public BatchExecuteResult() {
    success = new ArrayList<>();
    exceptions = new ArrayList<>();
    results = new ArrayList<>();
  }
}
