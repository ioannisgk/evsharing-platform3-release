package com.ioannisgk.evsharing.utils;

import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
 
public class QuartzTaskScheduler extends QuartzJobBean {
 
	// Class attributes
	
	private JobLocator jobLocator;
	private JobLauncher jobLauncher;
 
	// Class setters
	
	public void setJobLocator(JobLocator jobLocator) {
		this.jobLocator = jobLocator;
	}
 
	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}
 
	// Bridge Quartz and Spring Batch job
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		// Get job name from context data map
		
		Map<String, Object> jobMap = context.getMergedJobDataMap();
		String jobName = (String) jobMap.get("jobName");
		
		// Execute job with time parameters so that it can be restarted
		
		try {
			JobExecution execution = jobLauncher.run(jobLocator.getJob(jobName),
					new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis()).toJobParameters());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}