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
import net.kang.domain.Office;
import net.kang.repository.OfficeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@EnableMongoRepositories(basePackageClasses = {net.kang.repository.OfficeRepository.class})
@EntityScan(basePackageClasses = {net.kang.domain.Office.class})
public class OfficeRepositoryTest {
	@Autowired OfficeRepository officeRepository;
	static final int QTY=5; // 시구청의 수는 임시로 5개로 한다.
	static List<Office> tmpList;
	static Random random=new Random();

	@Before
	public void initialize() {
		tmpList=officeRepository.findAll();
		for(int k=0;k<QTY;k++) {
			Office office=new Office();
			office.setName(String.format("시구청%02d", k));
			office.setAddress(String.format("시청주소%02d", k));
			office.setHomepage(String.format("홈페이지%02d", k));
			office.setZipCode(String.format("우편번호%02d", k));
			officeRepository.insert(office);
		} // Mock Data에 대해서 각각 추가시킨다.
	}

	@Test
	public void findAllTest() { // findAll 테스팅. findAll를 한 결과 데이터의 목록의 수로 비교를 한다.
		List<Office> findAll=officeRepository.findAll();
		assertEquals(findAll.size(), tmpList.size()+QTY);
	}

	@Test
	public void findOneTest() { // findOne 테스팅. Mock Data에 대해 임의로 하나 꺼내서 테스팅을 한다.
		List<Office> findAll=officeRepository.findAll();
		int getIdx=tmpList.size()+random.nextInt(QTY);
		Office office=findAll.get(getIdx);
		Optional<Office> findOne=officeRepository.findById(office.getId());
		Office result=findOne.get();
		assertEquals(office, result);
	}

	@Test
	public void insertTest() { // insert 테스팅. 데이터를 추가하고 난 후에 그 데이터가 현존하는지에 대해 확인을 한다.
		List<Office> beforeInsertList=officeRepository.findAll();
		Office office=new Office();
		office.setName("시구청05");
		office.setAddress("시청주소05");
		office.setHomepage("홈페이지05");
		office.setZipCode("우편번호05");
		officeRepository.insert(office);
		List<Office> afterInsertList=officeRepository.findAll();
		afterInsertList.removeAll(beforeInsertList);
		assertTrue(officeRepository.existsById(afterInsertList.get(0).getId()));
	}

	@Test
	public void updateTest() { // update 테스팅. 데이터를 갱신하고 난 후에 그 현존하는 데이터가 올바르게 수정됐는지 확인을 한다.
		List<Office> beforeUpdateList=officeRepository.findAll();
		int getIdx=tmpList.size()+random.nextInt(QTY);
		Office office=beforeUpdateList.remove(getIdx);
		office.setName("시구청TEMP");
		office.setHomepage("홈페이지TEMP");
		office.setAddress("시청주소TEMP");
		office.setZipCode("우편번호TEMP");
		officeRepository.save(office);
		List<Office> afterUpdateList=officeRepository.findAll();
		assertTrue(afterUpdateList.contains(office));
	}

	@Test
	public void deleteTest() { // delete 테스팅. 데이터를 삭제하고 난 후에 데이터가 없어졌는지 확인을 한다.
		List<Office> beforeDeleteList=officeRepository.findAll();
		int getIdx=tmpList.size()+random.nextInt(QTY);
		Office office=beforeDeleteList.remove(getIdx);
		officeRepository.deleteById(office.getId());
		List<Office> afterDeleteList=officeRepository.findAll();
		assertTrue(!afterDeleteList.contains(office));
	}

	@Test
	public void deleteByNameContainingTest() { // deleteByNameContaining 테스팅. Mock 데이터들에 대해서 모두 삭제를 하고 현존하는 데이터의 수로 확인을 한다.
		List<Office> beforeDeleteList=officeRepository.findAll();
		officeRepository.deleteByNameContaining("시구청");
		List<Office> afterDeleteList=officeRepository.findAll();
		beforeDeleteList.removeAll(afterDeleteList);
		assertEquals(beforeDeleteList.size(), QTY);
	}

	@After
	public void afterTest() { // 테스팅이 완료되는 시점에서 Mock 데이터 목록들을 삭제한다.
		officeRepository.deleteByNameContaining("시구청");
	}
}
