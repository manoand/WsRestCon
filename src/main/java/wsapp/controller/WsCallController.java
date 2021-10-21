package wsapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wsapp.entity.Product;
import wsapp.service.HttpClientFactory;
import wsapp.service.HttpClientService;
import wsapp.utils.JSONUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
public class WsCallController {

    private Logger LOOGER = LoggerFactory.getLogger(WsCallController.class);

    @Autowired
    private HttpClientFactory httpClientFactory;

    @GetMapping(value = "/addProduct/{version}/{name}/{price}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> addProduct11(@PathVariable String version,@PathVariable String name,@PathVariable String price ) throws IOException, InterruptedException {
        Double productPrice;
        try
        {
            productPrice = Double.parseDouble(price);
        }
        catch(NumberFormatException e)
        {
            return new ResponseEntity<String>("Price is not a double :" + price, HttpStatus.BAD_REQUEST);
        }
        Product product = new Product();
        product.setName(name);
        product.setPrice(productPrice);

        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);

        product = httpClientService.callWsAddProduct(product);

        return new ResponseEntity<String>("Product added successfully :" + product.toString(), HttpStatus.CREATED);
    }

}
