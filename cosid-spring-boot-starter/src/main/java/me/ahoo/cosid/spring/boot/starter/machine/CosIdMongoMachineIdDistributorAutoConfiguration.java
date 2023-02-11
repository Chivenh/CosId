/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.cosid.spring.boot.starter.machine;

import me.ahoo.cosid.machine.ClockBackwardsSynchronizer;
import me.ahoo.cosid.machine.MachineStateStorage;
import me.ahoo.cosid.mongo.MachineCollection;
import me.ahoo.cosid.mongo.MachineInitializer;
import me.ahoo.cosid.mongo.MongoMachineCollection;
import me.ahoo.cosid.mongo.MongoMachineIdDistributor;
import me.ahoo.cosid.mongo.MongoMachineInitializer;
import me.ahoo.cosid.mongo.reactive.MongoReactiveMachineCollection;
import me.ahoo.cosid.mongo.reactive.MongoReactiveMachineInitializer;
import me.ahoo.cosid.spring.boot.starter.ConditionalOnCosIdEnabled;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * CosId Mongo MachineIdDistributor AutoConfiguration.
 *
 * @author ahoo wang
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnCosIdEnabled
@ConditionalOnCosIdMachineEnabled
@ConditionalOnClass(MongoMachineIdDistributor.class)
@ConditionalOnProperty(value = MachineProperties.Distributor.TYPE, havingValue = "mongo")
public class CosIdMongoMachineIdDistributorAutoConfiguration {
    private final MachineProperties machineProperties;
    
    public CosIdMongoMachineIdDistributorAutoConfiguration(MachineProperties machineProperties) {
        this.machineProperties = machineProperties;
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBean(MongoClient.class)
    public MachineInitializer machineInitializer(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(
            machineProperties.getDistributor().getMongo().getDatabase()
        );
        return new MongoMachineInitializer(mongoDatabase);
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(com.mongodb.reactivestreams.client.MongoClient.class)
    public MachineInitializer reactiveMachineInitializer(com.mongodb.reactivestreams.client.MongoClient mongoClient) {
        com.mongodb.reactivestreams.client.MongoDatabase mongoDatabase = mongoClient.getDatabase(
            machineProperties.getDistributor().getMongo().getDatabase()
        );
        return new MongoReactiveMachineInitializer(mongoDatabase);
    }
    
    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBean(MongoClient.class)
    public MachineCollection machineCollection(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(
            machineProperties.getDistributor().getMongo().getDatabase()
        );
        return new MongoMachineCollection(mongoDatabase.getCollection(MachineCollection.COLLECTION_NAME));
    }
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(com.mongodb.reactivestreams.client.MongoClient.class)
    public MachineCollection reactiveMachineCollection(com.mongodb.reactivestreams.client.MongoClient mongoClient) {
        com.mongodb.reactivestreams.client.MongoDatabase mongoDatabase = mongoClient.getDatabase(
            machineProperties.getDistributor().getMongo().getDatabase()
        );
        return new MongoReactiveMachineCollection(mongoDatabase.getCollection(MachineCollection.COLLECTION_NAME));
    }
    
    @Bean
    @ConditionalOnMissingBean
    public MongoMachineIdDistributor mongoMachineIdDistributor(MachineCollection machineCollection, MachineStateStorage localMachineState, ClockBackwardsSynchronizer clockBackwardsSynchronizer) {
        return new MongoMachineIdDistributor(machineCollection, localMachineState, clockBackwardsSynchronizer);
    }
    
}