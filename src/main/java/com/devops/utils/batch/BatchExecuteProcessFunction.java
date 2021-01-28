package com.devops.utils.batch;

@FunctionalInterface
public interface BatchExecuteProcessFunction<T, R> {
  public R apply(T input) throws Exception;
}
