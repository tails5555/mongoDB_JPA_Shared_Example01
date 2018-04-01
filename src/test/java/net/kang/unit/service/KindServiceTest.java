package net.kang.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;
import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.model.Position;
import net.kang.repository.KindRepository;
import net.kang.repository.ParkRepository;
import net.kang.service.KindService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class KindServiceTest {
	static final int KIND_QTY=5;
	static final int PARK_QTY=10; // 각각 종류와 공원의 Mock 데이터 수 설정
	MockMvc mockMvc;
	@Mock KindRepository kindRepository;
	@Mock ParkRepository parkRepository; // repository는 Mock 객체로
	@InjectMocks KindService kindService; // service 객체는 그대로 이용

	@Before
	public void initialize() { // Mock 객체 이용을 할 수 있도록 설정함
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(kindService).build();
	}

	public List<Kind> kindList(){ // 종류 Mock 데이터 반환
		List<Kind> kindList=new ArrayList<Kind>();
		for(int k=0;k<KIND_QTY;k++) {
			Kind kind=new Kind();
			kind.setId(String.format("%d", k+1));
			kind.setName(String.format("종류%02d", k));
			kindList.add(kind);
		}
		return kindList;
	}

	@Test
	public void findAllTest() { // findAll 테스팅
		List<Kind> tmpResult=kindList();
		when(kindRepository.findAll()).thenReturn(tmpResult);
		List<Kind> findAllResult=kindService.findAll();
		assertEquals(tmpResult, findAllResult);
	}

	public Kind findOneKind() { // 종류 Mock 객체 반환
		Kind kind=new Kind();
		kind.setId("1");
		kind.setName("종류01");
		return kind;
	}

	@Test
	public void findOneTest() { // findOne 테스팅
		Kind tmpResult=findOneKind();
		when(kindRepository.findById("1")).thenReturn(Optional.of(tmpResult));
		Optional<Kind> findOneResult=kindService.findOne("1");
		assertEquals(tmpResult, findOneResult.get());
	}

	public List<Park> parkList(Kind tmpKind){ // 공원 Mock 데이터 반환
		Agency agency=new Agency();
		agency.setId("1");
		agency.setName("기관01");
		List<Park> parkList=new ArrayList<Park>();
		for(int k=0;k<PARK_QTY;k++) {
			Park park=new Park();
			park.setId(String.format("%d", k+1));
			park.setName(String.format("공원%02d", k));
			park.setOldAddress(String.format("지번주소%02d", k));
			park.setNewAddress(String.format("도로명주소%02d", k));
			park.setCallPhone(String.format("전화번호%02d", k));
			park.setPosition(new Position(0.1, 0.1));
			park.setKind(tmpKind);
			park.setArea(0.0);
			park.setManageNo(String.format("00000-000%02d", k));
			park.setAgency(agency);
			park.setDesignateDate(new Date());
			parkList.add(park);
		}
		return parkList;
	}

	@Test
	public void findOneAndParkFindAllIsNotEmptyTest() { // 공원 조회 성공 테스팅
		Kind kind=findOneKind();
		List<Park> parkFindAll=parkList(kind);
		when(kindRepository.findById(kind.getId())).thenReturn(Optional.of(kind));
		when(parkRepository.findByKind(kind)).thenReturn(parkFindAll);
		assertEquals(parkFindAll, kindService.findOneAndParkFindAll(kind.getId()));
	}

	@Test
	public void findOneAndParkFindAllIsEmptyTest() { // 공원 조회 실패 테스팅
		Kind kind=findOneKind();
		List<Park> parkFindAll=new ArrayList<Park>();
		when(kindRepository.findById(kind.getId())).thenReturn(Optional.of(new Kind()));
		assertEquals(parkFindAll, kindService.findOneAndParkFindAll(kind.getId()));
	}

	@Test
	public void findByNameContainingTestIsNotEmptyTest() { // 이름 포함 조회 성공 테스팅
		List<Kind> kindList=kindList();
		when(kindRepository.findByNameContaining("종류")).thenReturn(kindList);
		assertEquals(kindList, kindService.findByNameContaining("종류"));
	}

	@Test
	public void findByNameContainingTestIsEmptyTest() { // 이름 포함 조회 실패 테스팅
		List<Kind> kindList=new ArrayList<Kind>();
		when(kindRepository.findByNameContaining("종료")).thenReturn(kindList);
		assertEquals(kindList, kindService.findByNameContaining("종료"));
	}

	@Test
	public void insertSuccessTest() { // 삽입 성공 테스팅
		Kind kind=findOneKind();
		when(kindRepository.existsById(kind.getId())).thenReturn(false);
		when(kindRepository.insert(kind)).thenReturn(kind);
		assertTrue(kindService.insert(kind));
	}

	@Test
	public void insertFailureTest() { // 삽입 실패 테스팅
		Kind kind=findOneKind();
		when(kindRepository.existsById(kind.getId())).thenReturn(true);
		assertFalse(kindService.insert(kind));
	}

	@Test
	public void updateSuccessTest() { // 갱신 성공 테스팅
		Kind kind=findOneKind();
		when(kindRepository.existsById(kind.getId())).thenReturn(true);
		kind.setName("종류TEMP");
		when(kindRepository.save(kind)).thenReturn(kind);
		assertTrue(kindService.update(kind));
	}

	@Test
	public void updateFailureTest() { // 갱신 실패 테스팅
		Kind kind=findOneKind();
		when(kindRepository.existsById(kind.getId())).thenReturn(false);
		assertFalse(kindService.update(kind));
	}

	@Test
	public void deleteSuccessTest() { // 삭제 성공 테스팅
		Kind kind=findOneKind();
		when(kindRepository.existsById(kind.getId())).thenReturn(true);
		doNothing().when(kindRepository).deleteById(kind.getId());
		assertTrue(kindService.delete(kind.getId()));
	}

	@Test
	public void deleteFailureTest() { // 삭제 실패 테스팅
		Kind kind=findOneKind();
		when(kindRepository.existsById(kind.getId())).thenReturn(false);
		assertFalse(kindService.delete(kind.getId()));
	}

	@Test
	public void deleteByNameContainingSuccessTest() { // 이름 포함 삭제 성공 테스팅
		List<Kind> tmpResult=kindList();
		when(kindRepository.findByNameContaining("종류")).thenReturn(tmpResult);
		doNothing().when(kindRepository).deleteByNameContaining("종류");
		assertTrue(kindService.deleteByNameContaining("종류"));
	}

	@Test
	public void deleteByNameContainingFailureTest() { // 이름 포함 삭제 실패 테스팅
		when(kindRepository.findByNameContaining("종료")).thenReturn(new ArrayList<Kind>());
		assertFalse(kindService.deleteByNameContaining("종료"));
	}
}
