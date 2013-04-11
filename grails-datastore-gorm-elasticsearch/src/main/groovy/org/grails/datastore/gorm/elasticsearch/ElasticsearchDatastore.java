package org.grails.datastore.gorm.elasticsearch;

import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.grails.datastore.mapping.core.AbstractDatastore;
import org.grails.datastore.mapping.core.Session;
import org.grails.datastore.mapping.model.MappingContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * Datastore implementation for Elasticsearch
 *
 * @author Lucas Ward
 */
public class ElasticsearchDatastore extends AbstractDatastore{

    private Client client;

    public ElasticsearchDatastore(MappingContext mappingContext, Map<String, String> connectionDetails, ConfigurableApplicationContext ctx, Client client) {
        super(mappingContext, connectionDetails, ctx);
        this.client = client;
        initializeConverters(mappingContext);
    }

    @Override
    protected Session createSession(Map<String, String> connectionDetails) {
        return new ElasticsearchSession(this, getMappingContext(), getApplicationEventPublisher());
    }

    @Override
    public boolean isSchemaless() {
        return true;
    }

    public Client getClient(){
        return client;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        client.close();
    }
}
