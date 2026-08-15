/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2018 the original author or authors.
 */
package org.assertj.swing.driver;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.assertj.core.util.Strings.concat;
import static org.assertj.swing.driver.JProgressBarSetIndetermintateTask.setIntedeterminate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.jspecify.annotations.NonNull;
import javax.swing.JProgressBar;

import org.assertj.swing.core.Robot;

/**
 * Asynchronously moves a {@code JProgressBar} to a determinate state, given an increment and a period of time in
 * between increments.
 * 
 * @author Alex Ruiz
 */
class JProgressBarMakeDeterminateAsyncTask {
  private static Logger logger = Logger.getAnonymousLogger();

  static @NonNull TaskBuilder makeDeterminate(@NonNull JProgressBar progressBar) {
    return new TaskBuilder(progressBar);
  }

  private final ExecutorService executor = newSingleThreadExecutor();
  private final Runnable task;
  private Future<?> future;

  private final Robot robot;
  private final JProgressBar progressBar;
  private final long periodInMs;

  private JProgressBarMakeDeterminateAsyncTask(@NonNull Robot robot, @NonNull JProgressBar progressBar, long periodInMs) {
    this.robot = robot;
    this.progressBar = progressBar;
    this.periodInMs = periodInMs;
    task = createInnerTask();
  }

  @NonNull private Runnable createInnerTask() {
    return new Runnable() {
      @Override
      public void run() {
        try {
          sleepAndMakeDeterminate();
        } catch (InterruptedException e) {
          logger.info("Task has been cancelled");
        }
      }
    };
  }

  private void sleepAndMakeDeterminate() throws InterruptedException {
    logger.info(concat("Going to sleep for ", periodInMs, " ms"));
    Thread.sleep(periodInMs);
    setIntedeterminate(progressBar, false);
    logger.info(concat("JProgressBar is in determinate state"));
    robot.waitForIdle();
  }

  synchronized void runAsynchronously() {
    future = executor.submit(task);
  }

  synchronized void cancelIfNotFinished() {
    if (future != null && !future.isDone()) {
      future.cancel(true);
    }
  }

  static class TaskBuilder {
    private final JProgressBar progressBar;
    private long periodInMs = 1000;

    TaskBuilder(@NonNull JProgressBar progressBar) {
      this.progressBar = progressBar;
    }

    @NonNull
    TaskBuilder after(long duration, @NonNull TimeUnit timeUnit) {
      periodInMs = timeUnit.toMillis(duration);
      return this;
    }

    @NonNull
    JProgressBarMakeDeterminateAsyncTask createTask(@NonNull Robot robot) {
      return new JProgressBarMakeDeterminateAsyncTask(robot, progressBar, periodInMs);
    }
  }
}
