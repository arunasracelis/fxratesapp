package com.fxratesapp.service;

import com.fxratesapp.model.Rate;
import com.fxratesapp.repository.RateRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.fxratesapp.utils.ExceptionUtils.findRootCause;

@Service
public class RateService {

    @Autowired
    private RateRepository rateRepository;
    private static final Logger logger = LogManager.getLogger(RateService.class);
    private static final String FX_RATES_URL = "http://www.lb.lt/webservices/FxRates/FxRates.asmx/getFxRatesForCurrency?tp=:TP&ccy=:CCY&dtFrom=:DTFROM&dtTo=:DTTO";
    private static final String CURRENT_FX_RATES_URL = "http://www.lb.lt//webservices/FxRates/FxRates.asmx/getCurrentFxRates?tp=EU";
    private static final String FX_TYPE = "EU";

    public List<Rate> findAll() {
        return rateRepository.findAll();
    }

    public List<Rate> findByCurrencyAndDate(String currency, String date) {
        return rateRepository.findByCurrency(currency, date);
    }

    public void save(Rate exchangeRate) {
        rateRepository.save(exchangeRate);
    }

    public void delete(Rate exchangeRate) {
        rateRepository.delete(exchangeRate);
    }

    public List<Rate> getFXRates(String currencyCode, String dateFrom, String dateTo) {
        String requestURL = FX_RATES_URL
                .replace(":TP", FX_TYPE)
                .replace(":CCY", currencyCode)
                .replace(":DTFROM", dateFrom)
                .replace(":DTTO", dateTo);
        return getRates(requestURL);
    }

    public List<Rate> getCurrentFXRates() {
        return getRates(CURRENT_FX_RATES_URL);
    }

    public Rate getPrevDayRate(String currencyCode) {
        LocalDate previousDay = LocalDate.now().minusDays(1);
        List<Rate> rates = getFXRates(currencyCode, previousDay.toString(), previousDay.toString());
        if (rates.size() != 0) {
            if (rates.get(0).getDate().equals(previousDay.toString())) {
                return rates.get(0);
            } else return null;
        } else return null;
    }

    private List<Rate> getRates(String requestURL) {
        List<Rate> rates = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(requestURL);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("FxRate");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Rate rate = new Rate();
                    rate.setDate(element.getElementsByTagName("Dt").item(0).getTextContent());
                    rate.setCurrency(element.getElementsByTagName("Ccy").item(1).getTextContent());
                    rate.setAmount(Double.parseDouble(element.getElementsByTagName("Amt").item(1).getTextContent()));
                    rates.add(rate);
                }
            }
            return rates;
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("Method: " +  new Object(){}.getClass().getEnclosingMethod().getName() + " in class: " +  getClass().getName() + " failed: " +  findRootCause(exception));
        }
        return null;
    }

}