package com.fxratesapp.controller;

import com.fxratesapp.model.Currency;
import com.fxratesapp.model.Rate;
import com.fxratesapp.service.CurrencyService;
import com.fxratesapp.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    CurrencyService currencyService;
    @Autowired
    RateService rateService;

    private final int DEFAULT_PERIOD_LENGTH = 14;
    private final String NO_DATA_MESSAGE = "No data is available for selected period. Showing closest available data.";
    private final String NO_RATE_MESSAGE = "Cannot convert to [currency]. Exchange rate is not found.";
    private final String DEFAULT_CURRENCY = "SEK";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DecimalFormat df = new DecimalFormat("###.######");

    @RequestMapping("/")
    public String showRates(@RequestParam(value = "currencyCode", required = false) String currencyCode,
                            @RequestParam(value = "daterange", required=false) String dateRange,
                            Model model) {

        if(currencyCode == null) {
            currencyCode = DEFAULT_CURRENCY;
        }

        if (dateRange == null) {
            // Creates default dateRange
            dateRange = formatter.format(LocalDate.now().minusDays(DEFAULT_PERIOD_LENGTH)) + " - " + formatter.format(LocalDate.now().minusDays(1));
        }

        String dateFrom = dateRange.substring(0,10);
        String dateTo = dateRange.substring(13,23);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("currencies", currencies);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("currencyName", currencyService.findNameByCode(currencies, currencyCode));
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);

        List<Rate> rates = rateService.getFXRates(currencyCode, dateFrom, dateTo);
        if (rates.isEmpty()) {
            model.addAttribute("rates", "NoData");
        } else {
            model.addAttribute("rates", rates);
            addUserMessage(model, dateFrom, rates);
        }
        return "index";
    }

    // Method adds userMessage to model if none of the found rates are in selected period
    private void addUserMessage(Model model, String dateFrom, List<Rate> rates) {
        if (!isRatesInSelectedPeriod(rates, dateFrom)) {
            model.addAttribute("userMessage", NO_DATA_MESSAGE);
        } else {
            model.addAttribute("userMessage", null);
        }
    }

    // Method checks if found rates are in selected period
    private boolean isRatesInSelectedPeriod(List<Rate> rates, String dateFrom) {
        for (Rate rate : rates) {
            if(LocalDate.parse(rate.getDate()).compareTo(LocalDate.parse(dateFrom)) >= 0) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/converter")
    public String showConverter(@RequestParam(value = "selectedCurrencyCode", required = false) String selectedCurrencyCode,
                                @RequestParam(value = "amount", required = false) String amount,
                                Model model) {

        //prepare model
        List<Currency> currencies = currencyService.getCurrencies();
        model.addAttribute("currencies", currencies);
        if(selectedCurrencyCode == null) selectedCurrencyCode = DEFAULT_CURRENCY;
        model.addAttribute("selectedCurrencyCode", selectedCurrencyCode);
        model.addAttribute("currencyName", currencyService.findNameByCode(currencies, selectedCurrencyCode));
        model.addAttribute("userMessage", null); // <== resets user message

        //calculate conversion result
        if (isAmountValid(amount)) {
            Rate prevDayRate = getPrevDayRate(selectedCurrencyCode);
            double eurToCCY;
            double ccyToEUR;
            if (prevDayRate != null)  {
                eurToCCY = prevDayRate.getAmount();
                ccyToEUR = 1/ eurToCCY;
                model.addAttribute("rateDate", prevDayRate.getDate());
            } else {
                model.addAttribute("amount", "");
                model.addAttribute("conversionResult", "");
                model.addAttribute("userMessage", NO_RATE_MESSAGE.replace("[currency]", selectedCurrencyCode));
                return "converter";
            }
            String conversionResult = df.format(Double.parseDouble(amount) * eurToCCY);
            model.addAttribute("amount", amount);
            model.addAttribute("conversionResult", conversionResult);
            model.addAttribute("eurToCCY", df.format(eurToCCY));
            model.addAttribute("ccyToEUR", df.format(ccyToEUR));
        } else {
            model.addAttribute("amount", "");
            model.addAttribute("conversionResult", "");
        }
        return "converter";
    }

    private boolean isAmountValid(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Rate getPrevDayRate(String currencyCode) {
        LocalDate previousDay = LocalDate.now().minusDays(1);
        List<Rate> rates = rateService.getFXRates(currencyCode, previousDay.toString(), previousDay.toString());
        if (rates.size() != 0) {
            return rates.get(0);
        } else return null;
    }

    @RequestMapping("/rates")
    public String showDailyRates(Model model) {
        model.addAttribute("rates", rateService.findAll());
        return "rates";
    }

}