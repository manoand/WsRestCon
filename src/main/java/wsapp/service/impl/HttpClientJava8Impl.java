package wsapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wsapp.entity.Product;
import wsapp.service.HttpClientService;
import wsapp.utils.Constants;
import wsapp.utils.JSONUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@Qualifier("HttpClientJava8impl")
public class HttpClientJava8Impl implements HttpClientService {
    private Logger LOOGER = LoggerFactory.getLogger(HttpClientJava8Impl.class);

    @Value("${service.url}")
    private String serviceURL;

    private URL addProductUrl;

    @Override
    public Product callWsAddProduct(Product product) {
        HttpURLConnection conAd = getAddProductConnection(product);
        StringBuilder response = getResponse(conAd);
        HttpURLConnection conGet = getGetProductConnection(response.toString());
        response = getResponse(conGet);
        return JSONUtils.covertFromJsonToObject(response.toString(), Product.class);
    }


    private URL getGetProductUrl(String id) {
        URL url = null;
        try {
            url = new URL(serviceURL + "getProduct/" + id);
        } catch (MalformedURLException e) {
            LOOGER.error("Creation of the URL for GetProdut failed",e);
        }
        return  url;
    }

    private URL getAddProductUrl() {
        if (addProductUrl == null) {
            try {
                addProductUrl = new URL(serviceURL + "addProduct");
            } catch (MalformedURLException e) {
                LOOGER.error("Creation of the URL for AddProduct failed",e);
            }
        }
        return addProductUrl;
    }

    private StringBuilder getResponse(HttpURLConnection con) {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), Constants.UTF8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        } catch (IOException e) {
            LOOGER.error("Unable to retrieve the response",e);
        }
        return response;
    }

    private HttpURLConnection getGetProductConnection(String id) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) getGetProductUrl(id).openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for GetProdut failed",e);
        }
        con.setRequestProperty("Accept", "application/json; " + Constants.UTF8);
        con.setDoOutput(true);

        return con;
    }

    private HttpURLConnection getAddProductConnection(Product product) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) getAddProductUrl().openConnection();
            con.setRequestMethod("POST");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for AddProduct failed",e);
        }
        con.setRequestProperty("Content-Type", "application/json; " + Constants.UTF8);
        con.setRequestProperty("Accept", "text/html");
        con.setDoOutput(true);

        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(product);

        //Sending the message
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = inputJson.getBytes(Constants.UTF8);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            LOOGER.error("AddProduct request failed ",e);
        }
        return con;
    }
}
