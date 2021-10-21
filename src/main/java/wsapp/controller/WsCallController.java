package wsapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wsapp.entity.Product;
import wsapp.utils.JSONUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class WsCallController {

    private final String serviceURL = "";

    @GetMapping(value = "/addProduct/{name}/{price}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> addProduct(@PathVariable String name,@PathVariable String price ) throws IOException, InterruptedException {
        Double productPrice;
        try
        {
            productPrice = Double.parseDouble(price);
        }
        catch(NumberFormatException e)
        {
            return new ResponseEntity<String>("Price is not a double :" + price, HttpStatus.BAD_REQUEST);
        }
        Product bean = new Product();
        bean.setName(name);
        bean.setPrice(productPrice);
        String inputJson = JSONUtils.covertFromObjectToJson(bean);
        HttpRequest requestAdd = HttpRequest.newBuilder(URI.create(serviceURL+"addProduct"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();

        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpResponse<String> responseAdd = httpClient
                .send(requestAdd, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestGet = HttpRequest.newBuilder(URI.create(serviceURL+"getProduct/"+responseAdd.body()))
                .header("Content-Type", "application/json")
                .GET().build();

        HttpResponse<String> responseGet = httpClient
                .send(requestGet, HttpResponse.BodyHandlers.ofString());

        bean = JSONUtils.covertFromJsonToObject(responseGet.body(),Product.class);

        return new ResponseEntity<String>("Product added successfully :" + bean.toString(), HttpStatus.CREATED);
    }
}
