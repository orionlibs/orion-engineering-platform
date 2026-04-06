package com.orion.engineering.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SpringSchedulerConfiguration {
	@Bean(name = "taskSchedulerForPipelineRuns")
	public TaskScheduler taskSchedulerForPipelineRuns() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(5);
		scheduler.setThreadNamePrefix("pipeline-run-scheduler-");
		scheduler.initialize();
		return scheduler;
	}
}
