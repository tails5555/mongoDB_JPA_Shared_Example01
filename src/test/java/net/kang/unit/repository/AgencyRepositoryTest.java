package net.kang.unit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;
import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.repository.AgencyRepository;
import net.kang.repository.OfficeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@EnableMongoRepositories(basePackageClasses = {net.kang.repository.AgencyRepository.class, net.kang.repository.OfficeRepository.class})
@EntityScan(basePackageClasses = {net.kang.domain.Agency.class, net.kang.domain.Office.class})
public class AgencyRepositoryTest {
	@Autowired AgencyRepository agencyRepository;
	@Autowired OfficeRepository officeRepository;
	static final int QTY=5;
	static Random random=new Random();
	static List<Office> tmpList;
	static List<Agency> beforeList;
	@Before
	public void initialize() {
		tmpList=officeRepository.findAll();
		beforeList=agencyRepository.findAll();
		List<Office> officeList=officeRepository.findAll();
		for(int k=0;k<QTY;k++) {
			int getIdx=random.nextInt(officeList.size());
			Agency agency=new Agency();
			agency.setName(String.format("기관%02d", k));
			agency.setOffice(officeList.get(getIdx));
			agencyRepository.insert(agency);
		}
	}

	@Test
	public void findAllTest() {
		List<Agency> agencyList=agencyRepository.findAll();
		assertEquals(agencyList.size(), beforeList.size()+QTY);
	}

	@Test
	public void findOneTest() {
		List<Agency> agencyList=agencyRepository.findAll();
		int getIndex=beforeList.size()+random.nextInt(QTY);
		Agency agency=agencyList.get(getIndex);
		Optional<Agency> findOne=agencyRepository.findById(agency.getId());
		assertEquals(agency, findOne.get());
	}

	@Test
	public void findByNameTest() {
		List<Agency> agencyList=agencyRepository.findAll();
		Agency agency=agencyRepository.findByName(String.format("기관%02d", random.nextInt(QTY))).get();
		assertTrue(agencyList.contains(agency));
	}

	@Test
	public void insertTest() {
		List<Agency> insertBeforeList=agencyRepository.findAll();
		Agency agency=new Agency();
		agency.setName("기관TEMP");
		List<Office> officeList=officeRepository.findAll();
		int getIdx=random.nextInt(officeList.size());
		agency.setOffice(officeList.get(getIdx));
		agencyRepository.insert(agency);
		List<Agency> insertAfterTest=agencyRepository.findAll();
		insertAfterTest.removeAll(insertBeforeList);
		assertTrue(agencyRepository.existsById(insertAfterTest.get(0).getId()));
	}

	@Test
	public void updateTest() {
		List<Agency> updateBeforeList=agencyRepository.findAll();
		int getIndex=beforeList.size()+random.nextInt(QTY);
		Agency agency=updateBeforeList.remove(getIndex);
		agency.setName("기관TEMP");
		List<Office> officeList=officeRepository.findAll();
		int getIdx=random.nextInt(officeList.size());
		agency.setOffice(officeList.get(getIdx));
		agencyRepository.save(agency);
		List<Agency> updateAfterList=agencyRepository.findAll();
		updateAfterList.removeAll(updateBeforeList);
		assertEquals(updateAfterList.get(0), agency);
	}

	@Test
	public void deleteTest() {
		List<Agency> deleteBeforeList=agencyRepository.findAll();
		int getIndex=beforeList.size()+random.nextInt(QTY);
		Agency agency=deleteBeforeList.remove(getIndex);
		agencyRepository.deleteById(agency.getId());
		List<Agency> deleteAfterList=agencyRepository.findAll();
		assertTrue(!deleteAfterList.contains(agency));
	}

	@Test
	public void deleteByNameContainingTest() {
		List<Agency> deleteBeforeList=agencyRepository.findAll();
		agencyRepository.deleteByNameContaining("기관");
		List<Agency> deleteAfterList=agencyRepository.findAll();
		assertEquals(deleteBeforeList.size()-QTY, deleteAfterList.size());
	}

	@Test
	public void findByOfficeTest() {
		int count=0;
		List<Agency> agencyList=agencyRepository.findAll();
		List<Office> officeList=officeRepository.findAll();
		for(Office o : officeList) {
			List<Agency> agencyOfficeList=agencyRepository.findByOffice(o);
			count+=agencyOfficeList.size();
		}
		assertEquals(agencyList.size(), count);
	}
	@After
	public void afterTest() {
		agencyRepository.deleteByNameContaining("기관");
	}
}
