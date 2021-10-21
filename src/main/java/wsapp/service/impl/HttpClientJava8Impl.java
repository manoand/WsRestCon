package wsapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wsapp.entity.Product;
import wsapp.service.HttpClientService;
import wsapp.utils.Constants;
import wsapp.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    public Product callWsAddProduct(Product product) throws IOException {
        HttpURLConnection conAd = getAddProductConnection(product);
        StringBuilder response = getResponse(conAd);
        HttpURLConnection conGet = getGetProductConnection(response.toString());
        response = getResponse(conGet);
        return JSONUtils.covertFromJsonToObject(response.toString(),Product.class);
    }


    private URL getGetProductUrl(String id) throws MalformedURLException {
        return new URL(serviceURL + "getProduct/"+id);
    }

    private URL getAddProductUrl() throws MalformedURLException {
        if( addProductUrl == null) {
            addProductUrl = new URL(serviceURL + "addProduct");
        }
        return addProductUrl;
    }

    private StringBuilder getResponse(HttpURLConnection con) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), Constants.UTF8))) {
            String responseLine ;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response;
    }

    private HttpURLConnection getGetProductConnection(String id) throws IOException {
        HttpURLConnection con = (HttpURLConnection) getGetProductUrl(id).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json; "+Constants.UTF8);
        con.setDoOutput(true);

        return con;
    }

    private HttpURLConnection getAddProductConnection(Product product) throws IOException {
        HttpURLConnection con = (HttpURLConnection) getAddProductUrl().openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; "+Constants.UTF8);
        con.setRequestProperty("Accept", "text/html");
        con.setDoOutput(true);

        String inputJson = JSONUtils.covertFromObjectToJson(product);

        //Sending the message
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = inputJson.getBytes(Constants.UTF8);
            os.write(input, 0, input.length);
        }
        return con;
    }
}
