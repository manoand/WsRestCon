package wsapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class HttpClientFactory {

    @Autowired
    @Qualifier("HttpClientJava11impl")
    private HttpClientService httpClientServiceJava11;

    @Autowired
    @Qualifier("HttpClientJava8impl")
    private HttpClientService httpClientServiceJava8;

    public HttpClientService getHttpClient(String version){
        HttpClientService httpClientService;
        switch (version){
            case"11":
                httpClientService = httpClientServiceJava11;
                break;
            case"8":
                httpClientService = httpClientServiceJava8;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + version);
        }
        return  httpClientService;
    }
}
