package wsapp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wsapp.entity.Product;
import wsapp.service.HttpClientService;
import wsapp.utils.JSONUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
@Qualifier("HttpClientJava11impl")
public class HttpClientJava11impl implements HttpClientService {

    private Logger LOGGER = LoggerFactory.getLogger(HttpClientJava11impl.class);

    @Value("${service.url}")
    private String serviceURL;

    private HttpRequest requestGetAllProduct;

    private HttpRequest getRequestGetAllProduct(){
        if(requestGetAllProduct == null) {
            requestGetAllProduct = HttpRequest.newBuilder(URI.create(serviceURL + "getDetails"))
                    .GET().build();
        }
        return requestGetAllProduct;
    }

    private final HttpClient httpClient = HttpClient.newBuilder().build();

    private HttpRequest getRequestAddProduct(String inputJson) {
        return HttpRequest.newBuilder(URI.create(serviceURL + "addProduct"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
    }

    private HttpRequest getRequestGetProduct(String id) {
        LOGGER.info("Call :" + serviceURL + "getProduct with id :" + id);
        return HttpRequest.newBuilder(URI.create(serviceURL + "getProduct/" + id))
                .GET().build();
    }

    private HttpRequest getRequestDeleteProduct(String id) {
        LOGGER.info("Call :" + serviceURL + "getProduct with id :" + id);
        return HttpRequest.newBuilder(URI.create(serviceURL + "deleteProduct/" + id))
                .DELETE().build();
    }

    private HttpResponse<String> getResponse(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            LOGGER.error("Following request failed :" + request.toString(), e);
        }
        LOGGER.info("Request : " + request.toString() + "response  : " + response.body());
        return response;
    }

    @Override
    public Product callGetProduct(String id) {
        HttpResponse<String> getProductResponse = getResponse(getRequestGetProduct(id));
        return JSONUtils.covertFromJsonToObject(getProductResponse.body(), Product.class);
    }

    @Override
    public List<Product> callGetAllProduct() {
        HttpResponse<String> getAllResponse = getResponse(getRequestGetAllProduct());
        return JSONUtils.convertFromJsonToList(getAllResponse.body(), new TypeReference<List<Product>>() {
        });
    }

    @Override
    public Product callWsAddProduct(Product product) {
        String inputJson = JSONUtils.covertFromObjectToJson(product);
        HttpResponse<String> addProductResponse = getResponse(getRequestAddProduct(inputJson));
        return callGetProduct(addProductResponse.body());
    }

    @Override
    public Product callDeleteProduct(String id) {
        Product product = null;
        HttpResponse<String> getProductResponse = getResponse(getRequestDeleteProduct(id));
        if(HttpStatus.SC_OK == getProductResponse.statusCode()){
            product =JSONUtils.covertFromJsonToObject(getProductResponse.body(), Product.class);
        }
        return product;
    }
}
