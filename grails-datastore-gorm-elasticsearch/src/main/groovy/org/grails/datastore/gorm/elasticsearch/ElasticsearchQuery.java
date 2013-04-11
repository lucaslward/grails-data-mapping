package org.grails.datastore.gorm.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.grails.datastore.mapping.core.Session;
import org.grails.datastore.mapping.model.PersistentEntity;
import org.grails.datastore.mapping.model.PersistentProperty;
import org.grails.datastore.mapping.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchQuery extends Query {

    private Client client;
    private ElasticsearchEntityPersister elasticsearchPersister;

    protected ElasticsearchQuery(Session session, PersistentEntity entity, Client client, ElasticsearchEntityPersister elasticsearchPersister) {
        super(session, entity);
        this.client = client;
        this.elasticsearchPersister = elasticsearchPersister;
    }

    @Override
    protected List executeQuery(PersistentEntity entity, Junction criteria) {
        String entityFamily = elasticsearchPersister.getEntityFamily();
        client.admin().indices().prepareRefresh(entityFamily).execute().actionGet();
        SearchResponse searchResponse = client.prepareSearch(entityFamily)
                .setTypes(entityFamily)
                .addFields(extractFieldNames(entity).toArray(new String[]{}))
                .setQuery(new MatchAllQueryBuilder())
                .execute()
                .actionGet();
        List<Projection> projectionList = projections().getProjectionList();

        List<Object> results = new ArrayList<Object>();

        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            Map<String, Object> nativeEntry = convertSearchHitToNativeEntry(hit);
            results.add(elasticsearchPersister.createObjectFromNativeEntry(entity, hit.getId(), nativeEntry));
        }

        return results;
    }

    //The 'fields' on a SearchHit is a map of the field name and a SearchHitField (which also contains the field name)
    private Map<String, Object> convertSearchHitToNativeEntry(SearchHit hit){
        Map<String, SearchHitField> fields = hit.fields();
        Map<String, Object> nativeEntry = new HashMap<String, Object>();
        for( Map.Entry<String, SearchHitField> entry : fields.entrySet() ){
            nativeEntry.put(entry.getKey(), entry.getValue().value());
        }
        return nativeEntry;
    }

    private List<String> extractFieldNames(PersistentEntity entity){
        List<String> fields = new ArrayList<String>();
        for(PersistentProperty property : entity.getPersistentProperties()){
            fields.add(property.getName());
        }
        return fields;
    }
}
