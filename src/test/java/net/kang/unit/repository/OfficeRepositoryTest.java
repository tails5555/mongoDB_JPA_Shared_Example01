package net.kang.unit.repository;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;
import net.kang.domain.Office;
import net.kang.repository.OfficeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@EnableMongoRepositories(basePackageClasses = {net.kang.repository.OfficeRepository.class})
@EntityScan(basePackageClasses = {net.kang.domain.Office.class})
public class OfficeRepositoryTest {
	@Autowired OfficeRepository officeRepository;
	static final int QTY=5;
	static List<Office> tmpList;
	static Random random=new Random();

	@Before
	public void initialize() {
		tmpList=officeRepository.findAll();
		for(int k=0;k<QTY;k++) {
			Office office=new Office();
			office.setName(String.format("시청%02d", k));
			office.setAddress(String.format("시청주소%02d", k));
			office.setHomepage("http://asdasf");
		}
	}

}
