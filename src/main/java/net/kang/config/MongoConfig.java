package net.kang.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration{

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private Integer mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Value("${spring.data.mongodb.username}")
    private String userName;

    @Value("${spring.data.mongodb.password}")
    private String password;

	@Override
	protected String getDatabaseName() {
		return mongoDatabase;
	}

	@Override
	public MongoClient mongoClient(){
		MongoCredential credential = MongoCredential.createCredential(userName, mongoDatabase, password.toCharArray());
		return new MongoClient(new ServerAddress(mongoHost, mongoPort), Arrays.asList(credential));
	}

	@Override
    public MongoDbFactory mongoDbFactory() {
    	return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
    }

	@Override
	public MongoMappingContext mongoMappingContext() throws ClassNotFoundException {
		return super.mongoMappingContext();
	}

	@Override
	public MongoTemplate mongoTemplate() throws Exception{
		MappingMongoConverter converter=new MappingMongoConverter(mongoDbFactory(), mongoMappingContext());
    	converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		MongoTemplate mongoTemplate=new MongoTemplate(mongoDbFactory(), converter);
		return mongoTemplate;
	}
}
