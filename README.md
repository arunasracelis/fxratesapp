# FX Rates App by [Arūnas Račelis][arunasracelis]

### Introduction

This web application fulfills the following functionality requirements:

* App has to show exchange rates using [Bank of Lithuania webservice][lblt]
* When user selects particular currency, app displays its rates history in a table format.
* App must include currency converter. When user enters amount and selects currency, the app displays the amount in foreign currency and exchange rate used.
* Exchange rates must be automatically retrieved everyday using Quartz, for example.
* Use H2 database.
* Use Java >= 1.7, Maven

```sh

```

### Frontend

* HTML
* CSS
* JS
* Bootstrap

### Backend

* Java 1.8
* Maven
* Spring Boot
* Quartz
* Thymeleaf
* H2
* Lombok


```sh

```

### Notice

* When app is started, it is accessible at standard http://localhost:8080/ 
* Converter is accessible at http://localhost:8080/converter
* Daily rates are accessible at http://localhost:8080/rates
* H2 database is accessible at http://localhost:8080/h2-console Username: sa Password: password

```sh

```

### Code Samples
```java
@Configuration
public class QuartzJobConfiguration {

    private static final Logger logger = LogManager.getLogger(QuartzJobConfiguration.class);
    private static final String CRON_SCHEDULE_EVERY_30_SECONDS = "0/30 * * ? * * *"; // <== for demo purposes only
    private static final String CRON_SCHEDULE_MON_FRI_7AM = "0 0 7 ? * MON,TUE,WED,THU,FRI *"; // <== http://www.cronmaker.com/

    @Bean
    public JobDetail jobDetails() {
        logger.info("Creating jobDetails bean in " + getClass().getName() + " class");
        return JobBuilder.newJob(QuartzJob.class).withIdentity("getFXRates").storeDurably().build();
    }

    @Bean
    public Trigger jobTrigger(JobDetail jobDetails) {
        logger.info("Creating jobTrigger bean in " + getClass().getName() + " class");
        return TriggerBuilder.newTrigger().forJob(jobDetails)
                .withIdentity("QuartzTriggerForDailyFXRates")
                .withSchedule(CronScheduleBuilder.cronSchedule(CRON_SCHEDULE_EVERY_30_SECONDS))
                .build();
    }

}
```

### Snapshots

#### Index

![alt text](https://github.com/arunasracelis/fxratesapp/blob/master/index.jpg)

#### Converter

![alt text](https://github.com/arunasracelis/fxratesapp/blob/master/converter.jpg)

#### Daily rates

![alt text](https://github.com/arunasracelis/fxratesapp/blob/master/dailyrates.jpg)

[arunasracelis]: <http://www.arunasracelis.com>
[lblt]: <http://www.lb.lt/webservices/FxRates/>
