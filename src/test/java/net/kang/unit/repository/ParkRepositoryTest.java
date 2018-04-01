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
public class ParkRepositoryTest { // 공원 Repository Unit Testing 클래스 생성.

	@Autowired ParkRepository parkRepository;
	@Autowired AgencyRepository agencyRepository;
	@Autowired KindRepository kindRepository;
	private final String[] tmpManageNoList= {"00000-00000", "00000-00001", "00000-00002", "00000-00003", "00000-00004"}; // 임시로 쓸 관리 번호
	static final int QTY=5; // 공원의 수는 임시로 5개로 한다.
	static Random random=new Random();

	public List<String> makeFacility(String s, int length){ // 임시로 시설 목록을 만들어준다.
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
		List<Agency> agencyList=agencyRepository.findAll(); // 각각 Kind와 Agency를 가져와서 Park에 설정을 할 수 있도록 한다.
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
		} // Mock Data에 대해 각각 추가시킨다.
	}

	@Test
	public void findAllTest() { // findAll 테스팅. findAll를 하고 Mock Data들이 정확히 들어갔는지 확인한다.
		List<Park> parkList=parkRepository.findAll();
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByManageNoTest() { // findByManageNo 테스팅. manageNo를 이용해서 찾은 결과에 대해 현존하는지 확인시킨다.
		int getIndex=random.nextInt(QTY);
		Optional<Park> park=parkRepository.findByManageNo(tmpManageNoList[getIndex]);
		assertTrue(parkRepository.existsById(park.get().getId()));
	}

	@Test // countByKind 테스팅. Kind 목록들을 토대로 공원 목록들을 조회하고 난 후에 현재 공원에 해당되는 목록의 수를 토대로 서로 합쳐서 일치한지에 대해 확인한다.
	public void countByKindTest() {
		List<Kind> kindList=kindRepository.findAll();
		long count=0;
		for(Kind k : kindList) {
			count+=parkRepository.countByKind(k);
		}
		assertEquals(QTY, count);
	}

	@Test
	public void countByAgencyTest() { // countByAgency 테스팅. Agency 목록들을 토대로 공원 목록들을 조회하고 난 후에 현재 공원에 해당되는 목록의 수를 토대로 서로 합쳐서 일치한지에 대해 확인한다.
		List<Agency> agencyList=agencyRepository.findAll();
		long count=0;
		for(Agency a : agencyList) {
			count+=parkRepository.countByAgency(a);
		}
		assertEquals(QTY, count);
	}

	@Test
	public void findByKindTest() { // findByKind 테스팅. countByKind와 맥락이 어느 정도 일치하지만, 여기서는 종류 객체를 이용해서 찾아서 서로 합쳐서 일치한가에 대해 작성을 하였다.
		List<Kind> kindList=kindRepository.findAll();
		int count=0;
		for(Kind k : kindList) {
			List<Park> parkList=parkRepository.findByKind(k);
			count+=parkList.size();
		}
		assertEquals(QTY, count);
	}


	@Test
	public void findByAgencyTest() { // findByAgency 테스팅. findByKind와 마찬가지다.
		List<Agency> agencyList=agencyRepository.findAll();
		int count=0;
		for(Agency a : agencyList) {
			List<Park> parkList=parkRepository.findByAgency(a);
			count+=parkList.size();
		}
		assertEquals(QTY, count);
	}

	@Test
	public void findByNameContainingTest() { // findByNameContaining 테스팅. Mock Data의 공원 이름은 공원00, 공원01... 등으로 저장이 되어 전체 목록 조회와 결과가 같은지 확인을 시킨다.
		List<Park> parkList=parkRepository.findByNameContaining("공원");
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByAreaBetweenTest() { // findByAreaBetween 테스팅. 공원 면적은 100에서 200사이의 데이터들이 들어온다. Mock Data에 있는 목록들이 일치하는가에 대해 확인을 시킨다.
		List<Park> parkList=parkRepository.findByAreaBetween(99.0, 201.0);
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByCultFacilityContainsTest() { // findByCultFacilityContains 테스팅. 문화 시설이 포함되어 있는지로 확인을 한다.
		List<Park> parkList=parkRepository.findByCultFacilityContains(new String[] {"문화00", "문화01"});
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void findByConvFacilityContainsTest() { // findByConvFacilityContains 테스팅. 문화 시설과 마찬가지로 편의 시설로 확인을 한다.
		List<Park> parkList=parkRepository.findByConvFacilityContains(new String[] {"편의00", "편의01"});
		assertEquals(QTY, parkList.size());
	}

	@Test
	public void deleteByManageNoTest() { // deleteByManageNo 테스팅. manageNo를 이용해서 데이터를 삭제를 하고 난 후에 데이터가 1개 삭제 되었는지 확인을 한다.
		parkRepository.deleteByManageNo("00000-00000");
		List<Park> parkList=parkRepository.findAll();
		assertEquals(QTY-1, parkList.size());
	}

	@Test
	public void insertTest() { // insert 테스팅. 데이터를 추가하고 난 후 findAll를 한 결과를 통해 확인한다.
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
	public void updateTest() { // update 테스팅. 데이터를 갱신하고 난 후에 그 현존하는 데이터가 올바르게 수정됐는지 확인을 한다.
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
	public void deleteTest() { // delete 테스팅. 데이터를 삭제하고 findAll를 한 결과로 확인을 한다.
		List<Park> parkList=parkRepository.findAll();
		Park park=parkList.get(random.nextInt(parkList.size()));
		parkRepository.deleteById(park.getId());
		assertEquals(parkRepository.findAll().size(), QTY-1);
	}

	@After
	public void afterTest() { // 테스팅이 완료되는 시점에서 Mock 데이터 목록들을 삭제한다.
		parkRepository.deleteAll();
	}

}
