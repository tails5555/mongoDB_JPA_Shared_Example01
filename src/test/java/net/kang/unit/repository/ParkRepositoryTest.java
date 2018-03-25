package net.kang.unit.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@EnableMongoRepositories(basePackageClasses = net.kang.repository.ParkRepository.class)
@EntityScan(basePackageClasses = net.kang.domain.Park.class)
public class ParkRepositoryTest {
}
