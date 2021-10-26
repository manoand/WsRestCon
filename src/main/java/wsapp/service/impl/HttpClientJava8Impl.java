package wsapp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wsapp.entity.Product;
import wsapp.service.HttpClientService;
import wsapp.utils.Constants;
import wsapp.utils.JSONUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
@Qualifier("HttpClientJava8impl")
public class HttpClientJava8Impl implements HttpClientService {
    private Logger LOOGER = LoggerFactory.getLogger(HttpClientJava8Impl.class);

    @Value("${service.url}")
    private String serviceURL;

    private URL addProductUrl;
    private URL allProductUrl;
    private URL updateProductUrl;

    @Override
    public Product callGetProduct(String id) {
        HttpURLConnection conGet = getGetProductConnection(id);
        StringBuilder jsonProduct = getResponse(conGet);
        conGet.disconnect();
        return JSONUtils.covertFromJsonToObject(jsonProduct.toString(), Product.class);
    }

    @Override
    public List<Product> callGetAllProduct() {
        HttpURLConnection con = getGetAllProductConnection();
        StringBuilder response = getResponse(con);
        con.disconnect();
        return JSONUtils.convertFromJsonToList(response.toString(), new TypeReference<List<Product>>() {
        });
    }

    @Override
    public Product callAddProduct(Product product) {
        HttpURLConnection con = getAddProductConnection(product);
        StringBuilder response = getResponse(con);
        con.disconnect();
        return callGetProduct(response.toString());
    }

    @Override
    public Product callDeleteProduct(String id) {
        HttpURLConnection con = getDeleteProductConnection(id);
        StringBuilder response = getResponse(con);
        con.disconnect();
        return JSONUtils.covertFromJsonToObject(response.toString(), Product.class);
    }

    @Override
    public Product callUpdateProduct(Product product) {
        HttpURLConnection con = getUpdateProductConnection(product);
        StringBuilder response = getResponse(con);
        con.disconnect();
        return JSONUtils.covertFromJsonToObject(response.toString(), Product.class);
    }

    private URL getDeleteProductUrl(String id) {
        URL url = null;
        try {
            url = new URL(serviceURL + "getProduct/" + id);
        } catch (MalformedURLException e) {
            LOOGER.error("Creation of the URL for GetProdut failed", e);
        }
        return url;
    }

    private URL getGetProductUrl(String id) {
        URL url = null;
        try {
            url = new URL(serviceURL + "getProduct/" + id);
        } catch (MalformedURLException e) {
            LOOGER.error("Creation of the URL for GetProdut failed", e);
        }
        return url;
    }

    @PostConstruct
    public void initURL() {
        try {
            addProductUrl = new URL(serviceURL + "addProduct");
            updateProductUrl = new URL(serviceURL + "updateProduct");
            allProductUrl = new URL(serviceURL + "getDetails");
        } catch (MalformedURLException e) {
            LOOGER.error("Creation of an URLfailed", e);
        }
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
            LOOGER.error("Unable to retrieve the response", e);
        }
        return response;
    }

    private HttpURLConnection getGetProductConnection(String id) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) getGetProductUrl(id).openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for GetProduct failed", e);
        }
        con.setRequestProperty("Accept", "application/json; " + Constants.UTF8);
        con.setDoOutput(true);

        return con;
    }

    private HttpURLConnection getDeleteProductConnection(String id) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) getDeleteProductUrl(id).openConnection();
            con.setRequestMethod("DELETE");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for deleteProduct failed", e);
        }
        con.setRequestProperty("Accept", "application/json; " + Constants.UTF8);
        con.setDoOutput(true);

        return con;
    }

    private HttpURLConnection getGetAllProductConnection() {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) allProductUrl.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for getDetails failed", e);
        }
        con.setRequestProperty("Accept", "application/json; " + Constants.UTF8);
        con.setDoOutput(true);

        return con;
    }

    private HttpURLConnection getAddProductConnection(Product product) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) addProductUrl.openConnection();
            con.setRequestMethod("POST");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for AddProduct failed", e);
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
            LOOGER.error("AddProduct request failed ", e);
        }
        return con;
    }

    private HttpURLConnection getUpdateProductConnection(Product product) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) updateProductUrl.openConnection();
            con.setRequestMethod("PUT");
        } catch (IOException e) {
            LOOGER.error("Creation of the connection for Product failed", e);
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
            LOOGER.error("Product request failed ", e);
        }
        return con;
    }
}
