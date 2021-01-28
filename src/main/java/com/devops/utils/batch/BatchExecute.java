package com.devops.utils.batch;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public class BatchExecute<I, R> {
  private final int batchSize;
  private final int timeOut;
  private List<I> inputs;
  private final List<R> outputs;
  private final Function<Integer, R> processFunction;
  public BatchExecuteResult<R> batchExecuteResult;

  public BatchExecute(BatchExecuteRequest<I, R> request) {
    this.inputs = request.inputs;
    this.outputs = new ArrayList<>(this.inputs.size());
    this.batchSize = request.batchSize;
    this.timeOut = request.timeOut;
    this.batchExecuteResult = new BatchExecuteResult<>();
    this.processFunction = (iteration) -> {
      try {
        this.batchExecuteResult.results.set(iteration, request.processFunction.apply(this.inputs.get(iteration)));
        this.batchExecuteResult.success.set(iteration, true);
      } catch (Exception e) {
        this.batchExecuteResult.success.set(iteration, false);
        this.batchExecuteResult.exceptions.set(iteration, e);
      }
      return null;
    };
  }

  private void handleThreads(List<Thread> threads) {
    if (timeOut != 0) {
      var thread = new Thread(() -> {
        try {
          Thread.sleep(timeOut);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
      thread.start();
      threads.add(thread);
    }
    threads.forEach(thread -> {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });
  }

  public BatchExecuteResult<R> execute() throws InterruptedException {
    // Initialize data
    for (int i = 0; i < inputs.size(); i++) {
      this.outputs.add(null);
      this.batchExecuteResult.results.add(null);
      this.batchExecuteResult.exceptions.add(null);
      this.batchExecuteResult.success.add(false);
    }

    // Initialize Collection to store batchSize threads
    var threads = new ArrayList<Thread>(batchSize);

    // Iterate over input data
    for (int i = 0; i < inputs.size(); i++) {
      // If batchSize threads have been spawned and are running, await them along with timeout limit
      if (i % batchSize == 0 && i != 0) {
        handleThreads(threads);
        threads = new ArrayList<>();
      }
      // Copy to iteration value to avoid using invalid i
      final int iteration = i;
      // create and start thread to process value for this iteration
      var thread = new Thread(() -> {
        this.outputs.set(iteration, this.processFunction.apply(iteration));
      });
      thread.start();
      // Add thread to Thread collection
      threads.add(thread);
    }
    // Await for remaining threads
    handleThreads(threads);

    return this.batchExecuteResult;
  }
}
