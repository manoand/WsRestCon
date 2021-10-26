package wsapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wsapp.entity.Product;
import wsapp.service.CallWsThread;
import wsapp.service.HttpClientFactory;
import wsapp.service.HttpClientService;

import java.util.List;
import java.util.Random;

@RestController
public class WsCallController {

    private Logger LOGGER = LoggerFactory.getLogger(WsCallController.class);

    @Autowired
    private HttpClientFactory httpClientFactory;

    @PostMapping(value = "/addProduct/{version}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> addProduct(@PathVariable String version, @RequestBody Product product) {
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        product = httpClientService.callAddProduct(product);
        return new ResponseEntity<String>("Product added successfully :" + System.lineSeparator() + product.toString(), HttpStatus.CREATED);
    }

    @GetMapping(value = "/getProduct/{version}/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getProduct(@PathVariable String version, @PathVariable String id) {

        try {
            Integer idProd = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<String>("Id is not a double :" + id, HttpStatus.BAD_REQUEST);
        }

        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);

        Product product = httpClientService.callGetProduct(id);

        return new ResponseEntity<String>("Product :" + product.toString(), HttpStatus.CREATED);

    }

    @GetMapping(value = "/getAll/{version}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getAllProduct(@PathVariable String version) {
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        List<Product> productList = httpClientService.callGetAllProduct();
        StringBuilder body = new StringBuilder();
        body.append("Product list :" + System.lineSeparator());
        for (Product product : productList) {
            body.append(product.toString());
            body.append(System.lineSeparator());
        }
        return new ResponseEntity<String>(body.toString(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/deleteProduct/{version}/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> deleteProduct(@PathVariable String version, @PathVariable String id) {
        try {
            Integer idProd = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return new ResponseEntity<String>("Id is not a double :" + id, HttpStatus.BAD_REQUEST);
        }
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        Product product = httpClientService.callDeleteProduct(id);
        return new ResponseEntity<String>("Deleted product :" + product.toString(), HttpStatus.OK);
    }

    @PutMapping(value = "/updateProduct/{version}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> updateProduct(@PathVariable String version, @RequestBody Product product) {
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        httpClientService.callUpdateProduct(product);
        return new ResponseEntity<String>("Updated product :" + product.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/FredLaunch/{version}/{nb}")
    public ResponseEntity<String> lancementDesFreds(@PathVariable String version, @PathVariable int nb){
        HttpClientService httpClientService = httpClientFactory.getHttpClient(version);
        for(int i = 0; i <= nb ;i++){
            CallWsThread callWsThread = new CallWsThread(httpClientService,nb);
            callWsThread.setName("Fred "+geneRandomName()+" "+i);
            callWsThread.start();
        }
        return new ResponseEntity<String>("Fin des Freds", HttpStatus.OK);
    }

    public String geneRandomName(){
        Random random = new Random();
        int nb = random.nextInt(4);
        String name = null;
        switch (nb){
            case 0:
                name="Le Flagorneur";
                break;
            case 1:
                name="L'Alambique";
                break;
            case 2:
                name="L'Abscon";
                break;
            case 3:
                name="Le Triptyque";
                break;
            default:
                name = "Le Defaut";
        }
        return name;
    }
}
