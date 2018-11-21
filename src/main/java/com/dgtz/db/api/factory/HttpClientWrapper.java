package com.dgtz.db.api.factory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Intellij IDEA.
 * User: Sardor Navruzov
 * Date: 1/30/13
 * Time: 1:15 PM
 */
public abstract class HttpClientWrapper {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientWrapper.class);

    protected HttpClientWrapper() {
    }


    protected Object doRequestGet(Class clazz, String subUrl) {
        Object ret = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
                /* Set client configurations */
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
            HttpGet getRequest = new HttpGet(subUrl);
            HttpResponse response = httpClient.execute(getRequest);

            if (response != null) {
                int code = response.getStatusLine().getStatusCode();

                if (HttpStatus.SC_OK == code) {
                    ret = getObjFromResp(response, clazz, subUrl);
                }
            }

            httpClient.getConnectionManager().shutdown();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("ERROR IN DB API ",e);
        }

        logger.debug(subUrl);
        return ret;
    }

    protected void doRequestGet(String url)
    {
        logger.debug("URL TO SEND {}", url);
        try {
            Unirest.get(url).asJson();
        } catch (Exception e){
            logger.error("ERROR IN SENDING PUSH", e);
        }
    }


    protected Object doRequestPut(Class clazz, String subUrl) {
        Object ret = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
                /* Set client configurations */
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
            HttpGet getRequest = new HttpGet(subUrl);
            HttpResponse response = httpClient.execute(getRequest);

            if (response != null) {
                int code = response.getStatusLine().getStatusCode();

                if (HttpStatus.SC_OK == code) {
                    ret = getObjFromResp(response, clazz, subUrl);
                }
            }

            httpClient.getConnectionManager().shutdown();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            logger.error("ERROR IN DB API ",e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("ERROR IN DB API ",e);
        }

        return ret;
    }

    @SuppressWarnings("rawtypes")
    private Object getObjFromResp(HttpResponse response, Class clazz, String subUrl) {
        Object ret = null;

        try {
            if (response != null) {
                JsonReader reader = new JsonReader(new InputStreamReader(response.getEntity().getContent()));

                System.out.println(reader.peek().toString());
                ret = new Gson().fromJson(reader, clazz);
            } else {
                logger.warn("Response from " + subUrl + " is null");
            }
        } catch (Exception e) {
            logger.error("ERROR IN DB API ",e);
        }

        return ret;
    }

}
