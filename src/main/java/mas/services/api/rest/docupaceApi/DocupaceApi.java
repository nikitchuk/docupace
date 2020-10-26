package mas.services.api.rest.docupaceApi;

import mas.services.api.utils.RESTLogger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;



public class DocupaceApi {

    private Client client = ClientBuilder.newClient().register(RESTLogger.class);
    protected final WebTarget docupace = client.target("ApiUrl");



}
