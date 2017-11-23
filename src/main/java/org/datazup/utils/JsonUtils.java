package org.datazup.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by admin@datazup on 11/24/16.
 */
public class JsonUtils {

        private static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

        public static Object getDeserializedFromBytes(byte[] buffer, Class<?> objType) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();

            try {
                Object o = mapper.readValue(buffer, objType);
                return o;
            } catch (JsonParseException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (JsonMappingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }

            return null;
        }

        public static byte[] getSerializedToBytes(Object value) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            try {
                byte[] o = mapper.writeValueAsBytes(value);
                return o;
            } catch (JsonParseException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (JsonMappingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }

            return null;
        }

        @SuppressWarnings("rawtypes")
        public static List getListFromJson(String jsonString) {
            return (List) getObjectFromJson(jsonString, List.class);
        }

        @SuppressWarnings("rawtypes")
        public static Map getMapFromJson(String jsonString) {
            return (Map) getObjectFromJson(jsonString, Map.class);
        }

        public static Object getObjectFromJson(String jsonString, Class<?> objType) {

            ObjectMapper mapper = new ObjectMapper();
            // mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            // false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.findAndRegisterModules();
            try {
                Object o = mapper.readValue(jsonString, objType);
                return o;
            } catch (JsonParseException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (JsonMappingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }

            return null;
        }

        public static <T> Object getObjectFromJson(String jsonString, Class<?> objType,
                                                   Map<Class<T>, Class<? extends T>> typeMap) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.findAndRegisterModules();


            Version version = Version.unknownVersion();// new Version(1, 0, 0,
            // "SNAPSHOT"); //
            // maven/OSGi style version
            SimpleModule module = new SimpleModule("MyModuleName", version);

            SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();

            if (null != typeMap) {
                for (Map.Entry<Class<T>, Class<? extends T>> entry : typeMap.entrySet()) {
                    resolver.addMapping(entry.getKey(), entry.getValue());
                }
            }
            module.setAbstractTypes(resolver);
            mapper.registerModule(module);
            try {
                Object o = mapper.readValue(jsonString, objType);
                return o;
            } catch (JsonParseException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (JsonMappingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }

            return null;
        }

        public static <T> Object getObjectFromJson(String jsonString, TypeReference<T> typeRef) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.findAndRegisterModules();
            try {
                Object o = mapper.readValue(jsonString, typeRef);
                return o;
            } catch (JsonParseException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (JsonMappingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }

            return null;
        }

        public static JsonNode getJsonNodeFromString(String jsonString) {
            ObjectMapper mapper = new ObjectMapper();
            // JsonFactory factory = mapper.getJsonFactory();
            JsonNode actualObj = null;
            mapper.findAndRegisterModules();
            try {
                actualObj = mapper.readTree(jsonString);
            } catch (JsonProcessingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }
            return actualObj;
        }

        public static JsonNode getJsonNodeByName(String name, JsonNode node) {
            JsonNode jsonNode = node.get(name);

            return jsonNode;
        }



        public static String getJsonFromObject(Object object) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            try {
                return mapper.writeValueAsString(object);
            } catch (JsonGenerationException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (JsonMappingException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            } catch (IOException e) {
                LOG.info(e.getMessage() + "; stack: " + Arrays.deepToString(e.getStackTrace()));
            }
            return null;

        }

        public static Object getConvertObjectFromObject(Object obj, Class<?> objType) {
            String str = getJsonFromObject(obj);
            return getObjectFromJson(str, objType);
        }
}
