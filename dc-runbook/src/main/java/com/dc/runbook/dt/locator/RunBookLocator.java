package com.dc.runbook.dt.locator;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.dc.runbook.RunBookException;
import com.dc.runbook.dt.domain.Location;
import com.dc.runbook.dt.domain.RunBook;
import com.dc.runbook.dt.yaml.RunBookReader;
import com.dc.util.file.FileReadWriteSupport;

public class RunBookLocator {

    public static RunBook locate(String path, Location location) {
        RunBook result;
        String runBookYaml;
        if(Location.Local == location) {
            runBookYaml = locateRunBookFileLocally(path);
        }
        else if(Location.Http == location) {
            runBookYaml = locateRunBookFileOverHttp(path);
        }
        else {
            throw new UnsupportedOperationException(location + " location type part not yet implemented");
        }
        result = RunBookReader.read(runBookYaml);
        return result;
    }

    private static String locateRunBookFileLocally(String path) {
        return new String(FileReadWriteSupport.readFile(new File(path)));
    }

    private static String locateRunBookFileOverHttp(String uri) {
        String responseBody;

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpget = new HttpGet(uri);
            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                @Override
                public String handleResponse(
                        final HttpResponse response) throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        throw new RunBookException("Unexpected response status: " + status + " while locating RunBook");
                    }
                }

            };
            responseBody = httpClient.execute(httpget, responseHandler);
        } catch (IOException e) {
            throw new RunBookException("Unable to locate RunBook for URI : " + uri, e);
        }
        return responseBody;
    }

}
