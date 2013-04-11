package org.grails.datastore.gorm

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.common.settings.Settings
import org.grails.datastore.gorm.elasticsearch.ElasticsearchDatastore
import org.grails.datastore.gorm.elasticsearch.ElasticsearchSession
import org.grails.datastore.gorm.events.AutoTimestampEventListener
import org.grails.datastore.gorm.events.DomainEventListener
import org.grails.datastore.mapping.core.Session

import org.elasticsearch.node.Node
import org.elasticsearch.node.NodeBuilder
import org.grails.datastore.mapping.document.config.DocumentMappingContext
import org.grails.datastore.mapping.model.MappingContext
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.transactions.DatastoreTransactionManager
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import org.springframework.util.StringUtils
import org.springframework.validation.Errors
import org.springframework.validation.Validator

/**
 * @author graemerocher
 */
class Setup {

    static ElasticsearchDatastore datastore
    static ElasticsearchSession session
    static Node node

    static destroy() {
        session.nativeInterface.dropDatabase()
        session.disconnect()
        datastore.destroy()
        //close the local node
        node.close()
    }

    static Session setup(classes) {

        //Create a local node for testing only
        NodeBuilder builder = new NodeBuilder()
        builder.local(true)
        builder.settings().put("index.store.type", "memory").build()
        builder.loadConfigSettings(false)
        node = builder.node()
        Client client = node.client()

        DocumentMappingContext mappingContext = new DocumentMappingContext("elasticsearch")

        ApplicationContext ctx = new GenericApplicationContext()
        ctx.refresh()

        for(cls in classes){
            mappingContext.addPersistentEntity(cls)
        }

        ElasticsearchDatastore datastore = new ElasticsearchDatastore(mappingContext, [:], ctx, client)
        def enhancer = new GormEnhancer(datastore, new DatastoreTransactionManager(datastore: datastore))
        enhancer.enhance()

        datastore.mappingContext.addMappingContextListener({ e -> enhancer.enhance e } as MappingContext.Listener)
        datastore.applicationContext.addApplicationListener new DomainEventListener(datastore)
        datastore.applicationContext.addApplicationListener new AutoTimestampEventListener(datastore)

        session = datastore.connect()

        PersistentEntity entity = datastore.mappingContext.persistentEntities.find { PersistentEntity e -> e.name.contains("TestEntity")}

        datastore.mappingContext.addEntityValidator(entity, [
                supports: { Class c -> true },
                validate: { o, Errors errors ->
                    if (!StringUtils.hasText(o.name)) {
                        errors.rejectValue("name", "name.is.blank")
                    }
                }
        ] as Validator)cd ..

        return session
    }
}
