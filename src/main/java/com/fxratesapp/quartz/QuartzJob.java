package com.fxratesapp.quartz;

import com.fxratesapp.model.Rate;
import com.fxratesapp.service.RateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static com.fxratesapp.utils.ExceptionUtils.findRootCause;

public class QuartzJob implements Job {

    @Autowired
    private RateService rateService;
    private static final Logger logger = LogManager.getLogger(QuartzJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        saveRatesToDB();
    }

    private void saveRatesToDB() {
        logger.info(new Date() + " - Starting scheduled job: " + new Object() {}.getClass().getEnclosingMethod().getName());
        int counter = 0;
        try {
            List<Rate> rates = rateService.getCurrentFXRates();
            for (Rate rate : rates) {
                if (!isDuplicateRate(rate)) {
                    rateService.save(rate);
                    logger.info(new Date() + " - Saved rate to H2 DB: " + rate.getDate() + " - " + rate.getCurrency() + " - " + rate.getAmount());
                    counter++;
                }
            }
        } catch (Exception exception) {
            logger.error("Method: " +  new Object() {}.getClass().getEnclosingMethod().getName() + " in class: " +  getClass().getName() + " failed: " +  findRootCause(exception));
        }
        logger.info(new Date() + " - Scheduled job " + new Object() {}.getClass().getEnclosingMethod().getName() + " successfully finished. New records count: " + counter);
    }

    // Method checks if rate for given currency and date already exists in DB. This ensures that no duplicates are saved.
    private boolean isDuplicateRate(Rate rate) {
        List<Rate> duplicateRates = rateService.findByCurrencyAndDate(rate.getCurrency(), rate.getDate());
        return duplicateRates.size() != 0;
    }

}
