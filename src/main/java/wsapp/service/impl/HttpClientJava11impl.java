package wsapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wsapp.controller.WsCallController;
import wsapp.entity.Product;
import wsapp.service.HttpClientService;
import wsapp.utils.JSONUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Qualifier("HttpClientJava11impl")
public class HttpClientJava11impl implements HttpClientService {

    private Logger LOOGER = LoggerFactory.getLogger(HttpClientJava11impl.class);

    @Value("${service.url}")
    private String serviceURL;

    private final HttpClient httpClient = HttpClient.newBuilder().build();


    private HttpRequest getRequestAddProduct(String inputJson) {
        return HttpRequest.newBuilder(URI.create(serviceURL + "addProduct"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
    }

    private HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        LOOGER.info("Request : " + request.toString() + "response  : " + response.toString());
        return response;
    }

    private HttpRequest getRequestGetProduct(String id) {
        LOOGER.info("Call :" + serviceURL + "getProduct with id :" + id);
        return HttpRequest.newBuilder(URI.create(serviceURL + "getProduct/" + id))
                .header("Content-Type", "application/json")
                .GET().build();
    }

    @Override
    public Product callWsAddProduct(Product product) throws IOException, InterruptedException {
        String inputJson = JSONUtils.covertFromObjectToJson(product);
        HttpResponse<String> addProductResponse = getResponse(getRequestAddProduct(inputJson));
        HttpResponse<String> getProductResponse = getResponse(getRequestGetProduct(addProductResponse.body()));
        return JSONUtils.covertFromJsonToObject(getProductResponse.body(), Product.class);

    }
}
