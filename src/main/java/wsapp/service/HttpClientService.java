package wsapp.service;

import wsapp.entity.Product;

import java.io.IOException;
import java.util.List;

public interface HttpClientService {

    Product callGetProduct(String id);
    List<Product> callGetAllProduct();
    Product callWsAddProduct(Product product);
    Product callDeleteProduct(String id);
    Product callUpdateProduct(Product product);
}
