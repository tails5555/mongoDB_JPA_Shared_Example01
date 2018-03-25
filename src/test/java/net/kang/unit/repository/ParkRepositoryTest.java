package net.kang.unit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
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
import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.repository.AgencyRepository;
import net.kang.repository.KindRepository;
import net.kang.repository.ParkRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@EnableMongoRepositories(basePackageClasses = {net.kang.repository.ParkRepository.class, net.kang.repository.AgencyRepository.class, net.kang.repository.KindRepository.class})
@EntityScan(basePackageClasses = {net.kang.domain.Park.class, net.kang.domain.Agency.class, net.kang.domain.Kind.class})
public class ParkRepositoryTest {

	@Autowired ParkRepository parkRepository;
	@Autowired AgencyRepository agencyRepository;
	@Autowired KindRepository kindRepository;
	private final String[] tmpManageNoList= {"00000-00000", "00000-00001", "00000-00002", "00000-00003", "00000-00004"};
	static final int QTY=5;
	static Random random=new Random();

	public List<String> makeFacility(String s, int length){
		List<String> tmpFacility=new ArrayList<String>();
		for(int k=0;k<length;k++) {
			tmpFacility.add(String.format("%s%02d", s, k));
		}
		return tmpFacility;
	}

	@Before
	public void initialize() {
		parkRepository.deleteAll();
		List<Kind> kindList=kindRepository.findAll();
		List<Agency> agencyList=agencyRepository.findAll();
		for(int k=0;k<QTY;k++) {
			Park park=new Park();
			park.setManageNo(tmpManageNoList[k]);
			park.setName(String.format("공원%02d", k));
			park.setKind(kindList.get(random.nextInt(kindList.size())));
			park.setOldAddress(String.format("지번주소%02d", k));
			park.setNewAddress(String.format("도로명주소%02d", k));
			park.setArea(100.0+random.nextDouble()*100);
			park.setConvFacility(makeFacility("편의", 2+random.nextInt(2)));
			park.setCultFacility(makeFacility("문화", 2+random.nextInt(2)));
			park.setDesignateDate(new Date());
			park.setAgency(agencyList.get(random.nextInt(agencyList.size())));
			park.setCallPhone("031-000-0000");
			parkRepository.insert(park);
		}
	}

	@Test
	public void findAllTest() {
		List<Park> parkList=parkRepository.findAll();
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByManageNoTest() {
		int getIndex=random.nextInt(QTY);
		Optional<Park> park=parkRepository.findByManageNo(tmpManageNoList[getIndex]);
		assertTrue(parkRepository.existsById(park.get().getId()));
	}

	@Test
	public void countByKindTest() {
		List<Kind> kindList=kindRepository.findAll();
		long count=0;
		for(Kind k : kindList) {
			count+=parkRepository.countByKind(k);
		}
		assertEquals(QTY, count);
	}

	@Test
	public void findByKindTest() {
		List<Kind> kindList=kindRepository.findAll();
		int count=0;
		for(Kind k : kindList) {
			List<Park> parkList=parkRepository.findByKind(k);
			count+=parkList.size();
		}
		assertEquals(QTY, count);
	}


	@Test
	public void findByAgencyTest() {
		List<Agency> agencyList=agencyRepository.findAll();
		int count=0;
		for(Agency a : agencyList) {
			List<Park> parkList=parkRepository.findByAgency(a);
			count+=parkList.size();
		}
		assertEquals(QTY, count);
	}

	@Test
	public void findByNameContainingTest() {
		List<Park> parkList=parkRepository.findByNameContaining("공원");
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByAreaBetweenTest() {
		List<Park> parkList=parkRepository.findByAreaBetween(99.0, 201.0);
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByCultFacilityContainsTest() {
		List<Park> parkList=parkRepository.findByCultFacilityContains(new String[] {"문화00", "문화01"});
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByConvFacilityContainsTest() {
		List<Park> parkList=parkRepository.findByConvFacilityContains(new String[] {"편의00", "편의01"});
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void deleteByManageNoTest() {
		parkRepository.deleteByManageNo("00000-00000");
		List<Park> parkList=parkRepository.findAll();
		assertEquals(QTY-1, parkList.size());
	}

	@Test
	public void insertTest() {
		List<Kind> kindList=kindRepository.findAll();
		List<Agency> agencyList=agencyRepository.findAll();
		Park park=new Park();
		park.setManageNo("00000-00005");
		park.setName(String.format("공원%02d", 5));
		park.setKind(kindList.get(random.nextInt(kindList.size())));
		park.setOldAddress(String.format("지번주소%02d", 5));
		park.setNewAddress(String.format("도로명주소%02d", 5));
		park.setArea(100.0+random.nextDouble()*100);
		park.setConvFacility(makeFacility("편의", 2+random.nextInt(2)));
		park.setCultFacility(makeFacility("문화", 2+random.nextInt(2)));
		park.setDesignateDate(new Date());
		park.setAgency(agencyList.get(random.nextInt(agencyList.size())));
		park.setCallPhone("031-000-0000");
		parkRepository.insert(park);
		List<Park> findAll=parkRepository.findAll();
		assertEquals(findAll.size(), QTY+1);
	}

	@Test
	public void updateTest() {
		List<Kind> kindList=kindRepository.findAll();
		List<Agency> agencyList=agencyRepository.findAll();
		List<Park> parkList=parkRepository.findAll();
		Park park=parkList.get(random.nextInt(parkList.size()));
		park.setName("공원Temp");
		park.setKind(kindList.get(random.nextInt(kindList.size())));
		park.setOldAddress("지번주소Temp");
		park.setNewAddress("도로명주소Temp");
		park.setArea(100.0+random.nextDouble()*100);
		park.setConvFacility(makeFacility("편의", 1+random.nextInt(3)));
		park.setCultFacility(makeFacility("문화", 1+random.nextInt(3)));
		park.setDesignateDate(new Date());
		park.setAgency(agencyList.get(random.nextInt(agencyList.size())));
		park.setCallPhone("031-000-0000");
		parkRepository.save(park);

		Park updatePark=parkRepository.findByManageNo(park.getManageNo()).get();
		assertEquals(updatePark, park);
	}

	@Test
	public void deleteTest() {
		List<Park> parkList=parkRepository.findAll();
		Park park=parkList.get(random.nextInt(parkList.size()));
		parkRepository.deleteById(park.getId());
		assertEquals(parkRepository.findAll().size(), QTY-1);
	}

	@After
	public void afterTest() {
		parkRepository.deleteAll();
	}

}
