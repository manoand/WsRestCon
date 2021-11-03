package wsapp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wsapp.entity.Product;
import wsapp.service.HttpClientService;
import wsapp.utils.JSONUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Service
@Qualifier("Apache")
public class HttpClientApache implements HttpClientService {

    private Logger LOGGER = LoggerFactory.getLogger(HttpClientApache.class);

    private PoolingHttpClientConnectionManager connManager;

    @Value("${conn.poolmanager.defaultmaxperroute}")
    private int defaultMaxPerRoute;

    @Value("${conn.poolmanager.maxconn}")
    private int maxConn;

    @Value("${service.url}")
    private String serviceURL;

    @PostConstruct
    private void init() {
        connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        connManager.setMaxTotal(maxConn);
    }

    @Override
    public Product callGetProduct(String id) {
        HttpGet get = new HttpGet(serviceURL + "getProduct/" + id);
        HttpResponse response = null;
        try (CloseableHttpClient client = getHttpClient()){
            response = client.execute(get);
        } catch (IOException e) {
            LOGGER.error("Get call failed, id:" + id, e);
        }
        return JSONUtils.covertFromJsonToObject(getStringFromResponse(response), Product.class);
    }

    @Override
    public List<Product> callGetAllProduct() {
        HttpGet get = new HttpGet(serviceURL + "getDetails");
        HttpResponse response = null;
        try (CloseableHttpClient client = getHttpClient()){
            response = client.execute(get);
        } catch (IOException e) {
            LOGGER.error("AllGet call failed", e);
        }
        return JSONUtils.convertFromJsonToList(getStringFromResponse(response), new TypeReference<List<Product>>() {
        });
    }

    @Override
    public Product callAddProduct(Product product) {
        HttpPost httpPost = new HttpPost(serviceURL + "addProduct");
        HttpResponse response = null;
        String json = JSONUtils.covertFromObjectToJson(product);
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = getHttpClient()) {
            response = client.execute(httpPost);
        } catch (IOException e) {
            LOGGER.error("Post call failed", e);
        }
        return JSONUtils.covertFromJsonToObject(getStringFromResponse(response), Product.class);
    }


    @Override
    public Product callDeleteProduct(String id) {
        HttpDelete httpDelete = new HttpDelete(serviceURL+"deleteProduct/"+id);
        HttpResponse response = null;
        try(CloseableHttpClient client = getHttpClient()){
            response = client.execute(httpDelete);
        } catch (IOException e) {
            LOGGER.error("Delete call failed, id:" + id, e);
        }
        return JSONUtils.covertFromJsonToObject(getStringFromResponse(response), Product.class);
    }

    @Override
    public Product callUpdateProduct(Product product) {
        HttpResponse response = null;
        HttpPut httpPut = new HttpPut(serviceURL+"updateProduct");
        String json = JSONUtils.covertFromObjectToJson(product);
        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPut.setEntity(stringEntity);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        try (CloseableHttpClient client = getHttpClient()){
            response = client.execute(httpPut);
        } catch (IOException e) {
            LOGGER.error("UpdateProduct call failed", e);
        }

        return JSONUtils.covertFromJsonToObject(getStringFromResponse(response), Product.class);

    }

    private String getStringFromResponse(HttpResponse response) {
        String body = null;
        try {
            body = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            LOGGER.error("Retrieve of the response failed, response:" + response.toString(), e);
        }
        return body;
    }

    private CloseableHttpClient getHttpClient() {
        CloseableHttpClient client
                = HttpClients.custom().setConnectionManager(connManager).build();
        return client;
    }

}
