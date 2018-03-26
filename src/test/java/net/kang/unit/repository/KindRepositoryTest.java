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
	static final int QTY=5;
	static List<Kind> tmpList;
	static Random random=new Random();

	@Before
	public void initialize() {
		tmpList=kindRepository.findAll();
		for(int k=0;k<QTY;k++) {
			Kind kind=new Kind();
			kind.setName(String.format("종류%02d", k));
			kindRepository.insert(kind);
		}
	}

	@Test
	public void findAllTest() {
		List<Kind> findAll=kindRepository.findAll();
		assertEquals(findAll.size(), tmpList.size()+QTY);
	}

	@Test
	public void findOneTest() {
		int getIndex=tmpList.size()+random.nextInt(QTY);
		List<Kind> findAll=kindRepository.findAll();
		Kind kind=kindRepository.findById(findAll.get(getIndex).getId()).get();
		assertEquals(kind, findAll.get(getIndex));
	}

	@Test
	public void findByNameTest() {
		List<Kind> findAll=kindRepository.findAll();
		Kind kind=kindRepository.findByName(String.format("종류%02d", random.nextInt(QTY))).get();
		assertTrue(findAll.contains(kind));
	}

	@Test
	public void insertTest() {
		List<Kind> insertBeforeFindAll=kindRepository.findAll();
		Kind kind=new Kind();
		kind.setName("종류05");
		kindRepository.insert(kind);
		List<Kind> insertAfterFindAll=kindRepository.findAll();
		assertEquals(insertBeforeFindAll.size()+1, insertAfterFindAll.size());
	}

	@Test
	public void updateTest() {
		int getIndex=tmpList.size()+random.nextInt(QTY);
		List<Kind> updateBeforeFindAll=kindRepository.findAll();
		Kind kind=updateBeforeFindAll.get(getIndex);
		kind.setName("종류TEMP");
		kindRepository.save(kind);

		Kind updateKind=kindRepository.findById(kind.getId()).get();
		assertEquals(kind, updateKind);
	}

	@Test
	public void deleteTest() {
		int getIndex=tmpList.size()+random.nextInt(QTY);
		List<Kind> deleteBeforeFindAll=kindRepository.findAll();
		Kind kind=deleteBeforeFindAll.get(getIndex);
		kindRepository.deleteById(kind.getId());

		List<Kind> deleteAfterFindAll=kindRepository.findAll();
		assertTrue(!deleteAfterFindAll.contains(kind));
	}

	@Test
	public void deleteByNameContainingTest() {
		List<Kind> deleteBeforeFindAll=kindRepository.findAll();
		kindRepository.deleteByNameContaining("종류");
		assertEquals(deleteBeforeFindAll.size()-QTY, tmpList.size());
	}

	@After
	public void afterTest() {
		kindRepository.deleteByNameContaining("종류");
	}
}
