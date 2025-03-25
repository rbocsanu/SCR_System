package com.broker.social_companion_system.operator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ThreadConfig {

    private final OperatorDistributor operatorDistributer;

    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner schedulingRunner(@Qualifier("taskExecutor") TaskExecutor executor) {
        log.info("Scheduling runner...");
        return new CommandLineRunner() {
            public void run(String... args) throws Exception {
                log.info("Running runner");
                executor.execute(operatorDistributer);
            }
        };
    }
}
