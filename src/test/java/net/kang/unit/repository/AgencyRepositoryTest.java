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
public class AgencyRepositoryTest { // 기관  Repository Unit Testing 클래스 생성
	@Autowired AgencyRepository agencyRepository;
	@Autowired OfficeRepository officeRepository;
	static final int QTY=5; // 기관의 수는 임시로 5개로 한다.
	static Random random=new Random();
	static List<Office> tmpList; // Office 관련 테스팅을 위한 목록
	static List<Agency> beforeList; // 이전 기관 목록들에 대해 저장을 하기 위해 쓰는 변수.
	@Before
	public void initialize() {
		tmpList=officeRepository.findAll();
		beforeList=agencyRepository.findAll(); // 각각 Office와 Agency에 대해서 findAll를 해서 저장을 시켜둔다.
		List<Office> officeList=officeRepository.findAll();
		for(int k=0;k<QTY;k++) {
			int getIdx=random.nextInt(officeList.size());
			Agency agency=new Agency();
			agency.setName(String.format("기관%02d", k));
			agency.setOffice(officeList.get(getIdx));
			agencyRepository.insert(agency);
		} // Mock Data에 대해서 각각 추가시킨다.
	}

	@Test
	public void findAllTest() { // findAll 테스팅. findAll를 한 결과 데이터의 목록의 수로 비교를 한다.
		List<Agency> agencyList=agencyRepository.findAll();
		assertEquals(agencyList.size(), beforeList.size()+QTY);
	}

	@Test
	public void findOneTest() { // findOne 테스팅. Mock Data에 대해 임의로 하나 꺼내서 테스팅을 한다.
		List<Agency> agencyList=agencyRepository.findAll();
		int getIndex=beforeList.size()+random.nextInt(QTY);
		Agency agency=agencyList.get(getIndex);
		Optional<Agency> findOne=agencyRepository.findById(agency.getId());
		assertEquals(agency, findOne.get());
	}

	@Test
	public void findByNameTest() { // findByName 테스팅. Mock Data에 대해 이름으로 검색을 해서 테스팅을 한다.
		List<Agency> agencyList=agencyRepository.findAll();
		Agency agency=agencyRepository.findByName(String.format("기관%02d", random.nextInt(QTY))).get();
		assertTrue(agencyList.contains(agency));
	}

	@Test
	public void insertTest() { // insert 테스팅. 데이터를 추가하고 난 후에 그 데이터가 현존하는지에 대해 확인을 한다.
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
	public void updateTest() { // update 테스팅. 데이터를 갱신하고 난 후에 그 현존하는 데이터가 올바르게 수정됐는지 확인을 한다.
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
	public void deleteTest() { // delete 테스팅. 데이터를 삭제하고 난 후에 데이터가 없어졌는지 확인을 한다.
		List<Agency> deleteBeforeList=agencyRepository.findAll();
		int getIndex=beforeList.size()+random.nextInt(QTY);
		Agency agency=deleteBeforeList.remove(getIndex);
		agencyRepository.deleteById(agency.getId());
		List<Agency> deleteAfterList=agencyRepository.findAll();
		assertTrue(!deleteAfterList.contains(agency));
	}

	@Test
	public void deleteByNameContainingTest() { // deleteByNameContaining 테스팅. Mock 데이터들에 대해서 모두 삭제를 하고 현존하는 데이터의 수로 확인을 한다.
		List<Agency> deleteBeforeList=agencyRepository.findAll();
		agencyRepository.deleteByNameContaining("기관");
		List<Agency> deleteAfterList=agencyRepository.findAll();
		assertEquals(deleteBeforeList.size()-QTY, deleteAfterList.size());
	}

	@Test // findByOffice 테스팅. Mock Data에 대해서 각각 데이터를 가져와서 각 Office 별로 조회를 하면서 현존하는 기관의 목록 수와 일치한지 확인을 한다.
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
	public void afterTest() { // 테스팅이 완료되는 시점에서 Mock 데이터 목록들을 삭제한다.
		agencyRepository.deleteByNameContaining("기관");
	}
}
