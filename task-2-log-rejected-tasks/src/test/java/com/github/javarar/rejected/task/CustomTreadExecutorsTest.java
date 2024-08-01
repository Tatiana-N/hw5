package com.github.javarar.rejected.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executor;
import java.util.logging.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CustomTreadExecutorsTest {
    private final Logger logger = Logger.getLogger(CustomThreadExecutors.class.getSimpleName());
    private ByteArrayOutputStream logContent;
    
    @BeforeEach
    public void setUp() {
        logContent = new ByteArrayOutputStream();
        StreamHandler customHandler = new StreamHandler(logContent, new SimpleFormatter());
        
        LogManager.getLogManager().reset();
        logger.addHandler(customHandler);
        logger.setUseParentHandlers(false); // Отключаем обработчики по умолчанию
    }
    
    
    
    @Test
    public void threadPoolDoesNotThrowExceptionOnQueueOverflow() {
        // 0 тредов чтобы не забрали таски которыми наполняем
        Executor executor = CustomThreadExecutors.logRejectedThreadPoolExecutor(3, 0);
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.out.println("наполнили очередь задач");
        
        executor.execute(() -> {
            System.out.println("лишняя задача");
        });
        // Принудительное завершение обработчика, чтобы все логи были записаны
        for (Handler handler : logger.getHandlers()) {
            handler.flush();
        }
    
        // Проверка логов на наличие сообщения об отклонении задачи
        String logOutput = logContent.toString();
        assertTrue(logOutput.contains("я не ругаюсь, что много задач, но это уже перебор"), "Expected log message about task rejection");
    
    }
}
