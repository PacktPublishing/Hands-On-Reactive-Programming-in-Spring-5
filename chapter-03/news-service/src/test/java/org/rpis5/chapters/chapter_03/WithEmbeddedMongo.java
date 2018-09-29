package org.rpis5.chapters.chapter_03;

import org.rpis5.chapters.chapter_03.news_service.dto.News;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.connection.*;
import com.mongodb.connection.netty.NettyStreamFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public interface WithEmbeddedMongo {
    AtomicReference<MongodExecutable> MONGO_HOLDER = new AtomicReference<>();

    @BeforeClass
    static void setUpMongo() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();

        String bindIp = "localhost";
        int port = 27017;
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.DEVELOPMENT)
                .net(new Net(bindIp, port, Network.localhostIsIPv6()))
                .build();

        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
        MONGO_HOLDER.set(mongodExecutable);
        mongodExecutable.start();
    }

    @AfterClass
    static void tearDownMongo() {
        MONGO_HOLDER.get().stop();
    }

    default MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost/news");
        MongoClientSettings.Builder builder = MongoClientSettings.builder()
                .streamFactoryFactory(NettyStreamFactory::new)
                .applyToClusterSettings((cs) -> cs
                        .applyConnectionString(connectionString))
                .applyToConnectionPoolSettings(cps -> cps
                        .applyConnectionString(connectionString))
                .applyToServerSettings(ss -> ss
                        .applyConnectionString(connectionString))
              // TODO: Do not work with JDK11 without the next line being commented (null is not allowed)
              //.credential(connectionString.getCredential())
                .applyToSslSettings(ss -> ss
                        .applyConnectionString(connectionString))
                .applyToSocketSettings(ss -> ss
                        .applyConnectionString(connectionString))
                .codecRegistry(fromRegistries(
                        MongoClients.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder()
                                .automatic(true)
                                .register(News.class)
                                .build())
                ));

        if (connectionString.getReadPreference() != null) {
            builder.readPreference(connectionString.getReadPreference());
        }
        if (connectionString.getReadConcern() != null) {
            builder.readConcern(connectionString.getReadConcern());
        }
        if (connectionString.getWriteConcern() != null) {
            builder.writeConcern(connectionString.getWriteConcern());
        }
        if (connectionString.getApplicationName() != null) {
            builder.applicationName(connectionString.getApplicationName());
        }

        return MongoClients.create(builder.build());
    }
}
