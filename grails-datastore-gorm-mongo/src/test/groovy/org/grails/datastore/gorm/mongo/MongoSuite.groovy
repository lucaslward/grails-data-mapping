package org.grails.datastore.gorm.mongo

import grails.gorm.tests.CrudOperationsSpec

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses
import grails.gorm.tests.PagedResultSpec
import grails.gorm.tests.EnumSpec
import grails.gorm.tests.OrderBySpec
import grails.gorm.tests.OneToManySpec

/**
 * @author graemerocher
 */
@RunWith(Suite)
@SuiteClasses([
//  DomainEventsSpec,
//  ProxyLoadingSpec,
//  QueryAfterPropertyChangeSpec,
//  CircularOneToManySpec,
//  InheritanceSpec,
//  FindByMethodSpec,
//  ListOrderBySpec,
//  GroovyProxySpec,
//  CommonTypesPersistenceSpec,
//  GormEnhancerSpec,
//  CriteriaBuilderSpec,
//  NegationSpec,
//  NamedQuerySpec,
//  OrderBySpec,
//  RangeQuerySpec,
//  ValidationSpec,
//  UpdateWithProxyPresentSpec,
//  AttachMethodSpec,
//  WithTransactionSpec,
//  CrudOperationsSpec
//    PagedResultSpec
//    EnumSpec
//    OrderBySpec
//    OneToManySpec
    EnumSpec
])
class MongoSuite {
}