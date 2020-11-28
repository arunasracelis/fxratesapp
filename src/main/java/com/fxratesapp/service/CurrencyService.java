package com.fxratesapp.service;

import com.fxratesapp.model.Currency;
import com.fxratesapp.repository.CurrencyRepository;
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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.fxratesapp.utils.ExceptionUtils.findRootCause;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;
    private static final Logger logger = LogManager.getLogger(CurrencyService.class);
    private static final String CURRENCY_LIST_REQUEST_URL = "http://www.lb.lt/webservices/FxRates/FxRates.asmx/getCurrencyList?";

    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    public String findNameByCode(List<Currency> currencies, String code) {
        Currency foundCurrency = currencies.stream()
                .filter(currency -> code.equals(currency.getCode()))
                .findAny()
                .orElse(null);
        if (foundCurrency != null)
            return foundCurrency.getName();
        else return null;
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencies = new ArrayList<>();
        try {
            URL url = new URL(CURRENCY_LIST_REQUEST_URL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla 5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.11) ");
            InputStream inputFile = urlConnection.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputFile);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("CcyNtry");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Currency currency = new Currency();
                    currency.setCode(element.getElementsByTagName("Ccy").item(0).getTextContent());
                    currency.setName(element.getElementsByTagName("CcyNm").item(1).getTextContent());
                    if (!currency.getCode().equalsIgnoreCase("EUR")) {
                        currencies.add(currency);
                    }
                }
            }
            return currencies;
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.error("Method: " +  new Object(){}.getClass().getEnclosingMethod().getName() + " in class: " +  getClass().getName() + " failed: " +  findRootCause(exception));
        }
        return null;
    }

}