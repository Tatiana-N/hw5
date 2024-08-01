package com.github.javarar.rejected.task;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class
CustomThreadExecutors implements  Executor{
    private final Logger logger = Logger.getLogger(CustomThreadExecutors.class.getSimpleName());
    private Queue<Runnable> tasks;
    private List<Thread> threads = new LinkedList<>();
    
    private CustomThreadExecutors(int sizeQueueTasks, int threadNumber) {
        tasks = new LinkedBlockingQueue<>(sizeQueueTasks);
        for (int i = 0; i < threadNumber; i++) {
            threads.add(new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    Runnable runnable = tasks.poll();
                    if(runnable != null) {
                        runnable.run();
                    }
                }
            }));
        }
        threads.forEach(Thread::start);
    }
    
    public static Executor logRejectedThreadPoolExecutor(Integer sizeQueueTasks, Integer threadNumber ) {
        return new CustomThreadExecutors(sizeQueueTasks, threadNumber);
    }
    
    @Override
    public void execute(Runnable command) {
            try{
                tasks.add(command);
            } catch (IllegalStateException e){
                logger.warning("я не ругаюсь, что много задач, но это уже перебор");
            }
        }
    }
