package wsapp.service;

import wsapp.entity.Product;

import java.io.IOException;
import java.util.List;

public interface HttpClientService {

    Product callGetProduct(String id);
    List<Product> callGetAllProduct();
    Product callAddProduct(Product product);
    Product callDeleteProduct(String id);
    Product callUpdateProduct(Product product);
}
