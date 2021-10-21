package wsapp.service;

import wsapp.entity.Product;

import java.io.IOException;

public interface HttpClientService {

    Product callWsAddProduct(Product product) throws IOException, InterruptedException;
}
