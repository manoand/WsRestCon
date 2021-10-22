package wsapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wsapp.entity.Product;
import wsapp.service.HttpClientFactory;
import wsapp.service.HttpClientService;

import java.util.List;

@RestController
public class WsCallController {

    private Logger LOOGER = LoggerFactory.getLogger(WsCallController.class);

    @Autowired
    private HttpClientFactory httpClientFactory;

    @GetMapping(value = "/addProduct/{version}/{name}/{price}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> addProduct(@PathVariable String version,@PathVariable String name,@PathVariable String price ) {
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

        return new ResponseEntity<String>("Product added successfully :"+System.lineSeparator() + product.toString(), HttpStatus.CREATED);
    }

    @GetMapping(value = "/getProduct/{version}/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getProduct(@PathVariable String version, @PathVariable String id) {

        try {
            Integer idProd = Integer.parseInt(id);
        }catch (NumberFormatException  e){
            return new ResponseEntity<String>("Id is not a double :" + id, HttpStatus.BAD_REQUEST);
        }

        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);

        Product product = httpClientService.callGetProduct(id);

        return new ResponseEntity<String>("Product :" + product.toString(), HttpStatus.CREATED);

    }

    @GetMapping(value = "getAll/{version}",produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getAllProduct(@PathVariable String version){
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        List<Product> productList = httpClientService.callGetAllProduct();
        StringBuilder body = new StringBuilder();
        body.append("Product list :"+System.lineSeparator());
        for(Product product : productList){
            body.append(product.toString());
            body.append( System.lineSeparator());
        }
        return new ResponseEntity<String>(body.toString(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteProduct/{version}/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> deleteProduct(@PathVariable String version, @PathVariable String id) {
        try {
            Integer idProd = Integer.parseInt(id);
        }catch (NumberFormatException  e){
            return new ResponseEntity<String>("Id is not a double :" + id, HttpStatus.BAD_REQUEST);
        }
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        Product product = httpClientService.callDeleteProduct(id);
        return new ResponseEntity<String>("Deleted product :"+ product.toString(), HttpStatus.OK);
    }
}
