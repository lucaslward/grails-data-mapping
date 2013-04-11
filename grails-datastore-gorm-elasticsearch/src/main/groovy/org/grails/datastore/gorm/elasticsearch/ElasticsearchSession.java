package org.grails.datastore.gorm.elasticsearch;

import org.elasticsearch.client.Client;
import org.grails.datastore.mapping.core.AbstractSession;
import org.grails.datastore.mapping.core.Datastore;
import org.grails.datastore.mapping.engine.Persister;
import org.grails.datastore.mapping.model.MappingContext;
import org.grails.datastore.mapping.model.PersistentEntity;
import org.grails.datastore.mapping.transactions.Transaction;
import org.springframework.context.ApplicationEventPublisher;

public class ElasticsearchSession extends AbstractSession<Client> {

    public ElasticsearchSession(Datastore datastore, MappingContext mappingContext, ApplicationEventPublisher publisher) {
        super(datastore, mappingContext, publisher);
    }

    @Override
    protected Persister createPersister(Class cls, MappingContext mappingContext) {
        PersistentEntity entity = mappingContext.getPersistentEntity(cls.getName());
        return entity == null ? null : new ElasticsearchEntityPersister(mappingContext, entity, this, getPublisher(), getNativeInterface());
    }

    @Override
    protected Transaction beginTransactionInternal() {
        //TODO - This will likely be implemented using the bulk API (or at least I can't think of any other way)
        return null;
    }

    @Override
    public Client getNativeInterface() {
        return ((ElasticsearchDatastore)getDatastore()).getClient();
    }
}
