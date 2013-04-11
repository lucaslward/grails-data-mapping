package org.grails.datastore.gorm.elasticsearch;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.flush.FlushRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.UUID;
import org.elasticsearch.indices.IndexMissingException;
import org.grails.datastore.mapping.core.Session;
import org.grails.datastore.mapping.engine.AssociationIndexer;
import org.grails.datastore.mapping.engine.EntityAccess;
import org.grails.datastore.mapping.engine.NativeEntryEntityPersister;
import org.grails.datastore.mapping.engine.PropertyValueIndexer;
import org.grails.datastore.mapping.model.MappingContext;
import org.grails.datastore.mapping.model.PersistentEntity;
import org.grails.datastore.mapping.model.PersistentProperty;
import org.grails.datastore.mapping.model.types.Association;
import org.grails.datastore.mapping.query.Query;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchEntityPersister extends NativeEntryEntityPersister<Map<String, Object>, String> {

    private Client client;
    private String entityFamily;

    public ElasticsearchEntityPersister(MappingContext mappingContext, PersistentEntity entity, Session session, ApplicationEventPublisher publisher, Client client) {
        super(mappingContext, entity, session, publisher);
        this.client = client;
        //todo: i don't think we want this name with the package prefix, but not super important right now
        entityFamily = entity.getName().toLowerCase();
    }

    @Override
    public String getEntityFamily() {
        return entityFamily;
    }

    @Override
    protected void deleteEntry(String family, String key, Object entry) {
        client.prepareDelete(family, family, key).execute().actionGet();
    }

    @Override
    protected String generateIdentifier(PersistentEntity persistentEntity, Map<String, Object> entry) {
        return UUID.randomUUID().toString();
    }

    @Override
    public PropertyValueIndexer getPropertyIndexer(PersistentProperty property) {
        return null;
    }

    @Override
    public AssociationIndexer getAssociationIndexer(Map<String, Object> nativeEntry, Association association) {
        return null;
    }

    @Override
    protected Map<String, Object> createNewEntry(String family) {
        return new HashMap<String,Object>();
    }

    @Override
    protected Object getEntryValue(Map<String, Object> nativeEntry, String property) {
        return nativeEntry.get(property);
    }

    @Override
    protected void setEntryValue(Map<String, Object> nativeEntry, String key, Object value) {
        nativeEntry.put(key, value);
    }

    @Override
    protected Map<String, Object> retrieveEntry(PersistentEntity persistentEntity, String family, Serializable key) {
        GetResponse response = null;
        try {
            response = client.prepareGet(family, family, key.toString()).execute().actionGet();
        } catch (IndexMissingException e) {
            return null;
        }
        return response.getSource();
    }

    @Override
    protected String storeEntry(PersistentEntity persistentEntity, EntityAccess entityAccess, String storeId, Map<String, Object> nativeEntry) {
        IndexRequestBuilder builder = client.prepareIndex(entityFamily, entityFamily, storeId);
        builder.setSource(nativeEntry);
        IndexResponse response = builder.execute().actionGet();
        return response.getId();
    }

    @Override
    protected void updateEntry(PersistentEntity persistentEntity, EntityAccess entityAccess, String key, Map<String, Object> entry) {
    }

    @Override
    protected void deleteEntries(String family, List<String> keys) {
    }

    @Override
    public Query createQuery() {
        return new ElasticsearchQuery(getSession(), getPersistentEntity(), client, this);
    }
}
