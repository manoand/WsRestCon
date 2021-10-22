package wsapp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class JSONUtils {
    //convert JSON into List of Objects

    private static Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);
    
    static public <T> List<T> convertFromJsonToList(String json, TypeReference<List<T>> var) {
        ObjectMapper mapper = new ObjectMapper();
        List<T> objectList = null;
        try {
            objectList = mapper.readValue(json, var);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json list "+json+" parsing failed",e);
        }
        return objectList;
    }

    static public <T> T covertFromJsonToObject(String json, Class<T> var) {
        ObjectMapper mapper = new ObjectMapper();
        T object = null;
        try {
            object = mapper.readValue(json, var);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json "+json+" parsing failed",e);
        }
        return object;
    }

    //convert Object into JSON
    public static String covertFromObjectToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonObject = null;
        try {
            jsonObject =mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error("Objct "+obj.getClass().getName()+ " parsing failed",e);
        }
        return jsonObject;
    }


}
