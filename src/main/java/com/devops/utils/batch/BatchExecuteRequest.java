package com.devops.utils.batch;

import lombok.*;

import java.util.List;

@ToString
@AllArgsConstructor
@Builder
public class BatchExecuteRequest<I, R> {
  int batchSize;
  int timeOut;
  List<I> inputs;
  BatchExecuteProcessFunction<I, R> processFunction;
}
