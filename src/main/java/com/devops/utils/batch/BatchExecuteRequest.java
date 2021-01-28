package com.devops.utils.batch;

import lombok.*;

import java.util.List;
import java.util.function.Function;

@ToString
@AllArgsConstructor
@Builder
public class BatchExecuteRequest<I, R> {
  int batchSize;
  int timeOut;
  List<I> inputs;
  Function<I, R> processFunction;
}
