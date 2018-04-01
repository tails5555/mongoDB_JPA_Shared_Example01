package net.kang.unit.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
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
import net.kang.domain.Kind;
import net.kang.repository.KindRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@EnableMongoRepositories(basePackageClasses = {net.kang.repository.KindRepository.class})
@EntityScan(basePackageClasses = {net.kang.domain.Kind.class})
public class KindRepositoryTest {
	@Autowired KindRepository kindRepository;
	static final int QTY=5; // 종류의 수는 임시로 5개로 한다.
	static List<Kind> tmpList;
	static Random random=new Random();

	@Before
	public void initialize() {
		tmpList=kindRepository.findAll();
		for(int k=0;k<QTY;k++) {
			Kind kind=new Kind();
			kind.setName(String.format("종류%02d", k));
			kindRepository.insert(kind);
		} // Mock Data에 대해서 각각 추가시킨다.
	}

	@Test
	public void findAllTest() { // findAll 테스팅. findAll를 한 결과 데이터의 목록의 수로 비교를 한다.
		List<Kind> findAll=kindRepository.findAll();
		assertEquals(findAll.size(), tmpList.size()+QTY);
	}

	@Test
	public void findOneTest() { // findOne 테스팅. Mock Data에 대해 임의로 하나 꺼내서 테스팅을 한다.
		int getIndex=tmpList.size()+random.nextInt(QTY);
		List<Kind> findAll=kindRepository.findAll();
		Kind kind=kindRepository.findById(findAll.get(getIndex).getId()).get();
		assertEquals(kind, findAll.get(getIndex));
	}

	@Test
	public void findByNameTest() { // findByName 테스팅. Mock Data에 대해 이름으로 검색을 해서 테스팅을 한다.
		List<Kind> findAll=kindRepository.findAll();
		Kind kind=kindRepository.findByName(String.format("종류%02d", random.nextInt(QTY))).get();
		assertTrue(findAll.contains(kind));
	}

	@Test
	public void insertTest() { // insert 테스팅. 데이터를 추가하고 난 후에 그 데이터가 현존하는지에 대해 확인을 한다.
		List<Kind> insertBeforeFindAll=kindRepository.findAll();
		Kind kind=new Kind();
		kind.setName("종류05");
		kindRepository.insert(kind);
		List<Kind> insertAfterFindAll=kindRepository.findAll();
		assertEquals(insertBeforeFindAll.size()+1, insertAfterFindAll.size());
	}

	@Test
	public void updateTest() { // update 테스팅. 데이터를 갱신하고 난 후에 그 현존하는 데이터가 올바르게 수정됐는지 확인을 한다.
		int getIndex=tmpList.size()+random.nextInt(QTY);
		List<Kind> updateBeforeFindAll=kindRepository.findAll();
		Kind kind=updateBeforeFindAll.get(getIndex);
		kind.setName("종류TEMP");
		kindRepository.save(kind);

		Kind updateKind=kindRepository.findById(kind.getId()).get();
		assertEquals(kind, updateKind);
	}

	@Test
	public void deleteTest() { // delete 테스팅. 데이터를 삭제하고 난 후에 데이터가 없어졌는지 확인을 한다.
		int getIndex=tmpList.size()+random.nextInt(QTY);
		List<Kind> deleteBeforeFindAll=kindRepository.findAll();
		Kind kind=deleteBeforeFindAll.get(getIndex);
		kindRepository.deleteById(kind.getId());

		List<Kind> deleteAfterFindAll=kindRepository.findAll();
		assertTrue(!deleteAfterFindAll.contains(kind));
	}

	@Test
	public void deleteByNameContainingTest() { // deleteByNameContaining 테스팅. Mock 데이터들에 대해서 모두 삭제를 하고 현존하는 데이터의 수로 확인을 한다.
		List<Kind> deleteBeforeFindAll=kindRepository.findAll();
		kindRepository.deleteByNameContaining("종류");
		assertEquals(deleteBeforeFindAll.size()-QTY, tmpList.size());
	}

	@After
	public void afterTest() { // 테스팅이 완료되는 시점에서 Mock 데이터 목록들을 삭제한다.
		kindRepository.deleteByNameContaining("종류");
	}
}
