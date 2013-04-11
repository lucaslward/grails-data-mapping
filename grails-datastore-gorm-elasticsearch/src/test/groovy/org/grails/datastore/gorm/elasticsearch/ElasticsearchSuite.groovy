package org.grails.datastore.gorm.elasticsearch

import grails.gorm.tests.CrudOperationsSpec
import grails.gorm.tests.EnumSpec
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

/**
 * @author graemerocher
 */
@RunWith(Suite)
@SuiteClasses([
    CrudOperationsSpec
])
class ElasticsearchSuite {
}