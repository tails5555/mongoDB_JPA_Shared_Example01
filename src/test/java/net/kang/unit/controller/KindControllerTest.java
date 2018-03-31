package net.kang.unit.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URLEncoder;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.kang.config.JUnitConfig;
import net.kang.config.MongoConfig;
import net.kang.controller.KindController;
import net.kang.domain.Agency;
import net.kang.domain.Kind;
import net.kang.domain.Park;
import net.kang.model.Position;
import net.kang.service.KindService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class KindControllerTest {
	static final int KIND_QTY=5;
	static final int PARK_QTY=10;
	MockMvc mockMvc;
	@Mock KindService kindService;
	@InjectMocks KindController kindController;

	private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

	private List<Kind> kindList(){
		List<Kind> kindList=new ArrayList<Kind>();
		for(int k=0;k<KIND_QTY;k++) {
			Kind kind=new Kind();
			kind.setId(String.format("%d", k+1));
			kind.setName(String.format("종류%02d", k));
			kindList.add(kind);
		}
		return kindList;
	}

	private Kind findOneKind() {
		Kind kind=new Kind();
		kind.setId("1");
		kind.setName("종류00");
		return kind;
	}

	private List<Park> parkList(Kind tmpKind){
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

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(kindController).build();
	}

	@Test
	public void findAllSuccessTest() throws Exception{
		List<Kind> tmpList=kindList();
		when(kindService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/kind/findAll"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findAll();
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findAllFailureTest() throws Exception{
		List<Kind> tmpList=new ArrayList<Kind>();
		when(kindService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/kind/findAll"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findAll();
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findOneSuccessTest() throws Exception{
		Optional<Kind> tmpResult=Optional.of(findOneKind());
		when(kindService.findOne("1")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/kind/findOne/{id}", 1))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findOne("1");
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findOneFailureTest() throws Exception{
		Optional<Kind> tmpResult=Optional.of(new Kind());
		when(kindService.findOne("1")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/kind/findOne/{id}", 1))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findOne("1");
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findOneAndParkFindAllSuccessTest() throws Exception {
		Kind tmpKind=findOneKind();
		List<Park> tmpParkList=parkList(tmpKind);
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(kindService.findOneAndParkFindAll("1")).thenReturn(tmpParkList);
		mockMvc.perform(get("/kind/findOne/parkList/{id}", 1))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findOneAndParkFindAll("1");
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findOneAndParkFindAllFailureTest() throws Exception {
		List<Park> tmpParkList=new ArrayList<Park>();
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(kindService.findOneAndParkFindAll("1")).thenReturn(tmpParkList);
		mockMvc.perform(get("/kind/findOne/parkList/{id}", 1))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findOneAndParkFindAll("1");
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findByNameSuccessTest() throws Exception{
		Optional<Kind> tmpResult=Optional.of(findOneKind());
		String param=URLEncoder.encode("종류00", "UTF-8");
		when(kindService.findByName(param)).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/kind/findByName/{name}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findByName(param);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findByNameFailureTest() throws Exception{
		Optional<Kind> tmpResult=Optional.of(new Kind());
		String param=URLEncoder.encode("종류00", "UTF-8");
		when(kindService.findByName(param)).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/kind/findByName/{name}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findByName(param);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findByNameContainingSuccessTest() throws Exception{
		List<Kind> tmpParkList=kindList();
		String param=URLEncoder.encode("종류", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(kindService.findByNameContaining(param)).thenReturn(tmpParkList);
		mockMvc.perform(get("/kind/findByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void findByNameContainingFailureTest() throws Exception{
		List<Kind> tmpParkList=new ArrayList<Kind>();
		String param=URLEncoder.encode("종류", "UTF-8");
		String toJSON=this.jsonStringFromObject(tmpParkList);
		when(kindService.findByNameContaining(param)).thenReturn(tmpParkList);
		mockMvc.perform(get("/kind/findByNameContaining/{name}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(kindService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void insertSuccessTest() throws Exception {
		Kind kind=findOneKind();
		when(kindService.insert(kind)).thenReturn(true);
		String requestKind=this.jsonStringFromObject(kind);

		mockMvc.perform(
				post("/kind/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestKind))
		.andExpect(status().isCreated())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).insert(kind);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void insertFailureTest() throws Exception{
		Kind kind=findOneKind();
		when(kindService.insert(kind)).thenReturn(false);
		String requestKind=this.jsonStringFromObject(kind);

		mockMvc.perform(
				post("/kind/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestKind))
		.andExpect(status().isConflict())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).insert(kind);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void updateSuccessTest() throws Exception {
		Kind kind=findOneKind();
		when(kindService.update(kind)).thenReturn(true);
		String requestKind=this.jsonStringFromObject(kind);

		mockMvc.perform(
				put("/kind/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestKind))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).update(kind);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void updateFailureTest() throws Exception{
		Kind kind=findOneKind();
		when(kindService.update(kind)).thenReturn(false);
		String requestKind=this.jsonStringFromObject(kind);

		mockMvc.perform(
				put("/kind/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestKind))
		.andExpect(status().isNotModified())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).update(kind);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void deleteSuccessTest() throws Exception {
		when(kindService.delete("1")).thenReturn(true);
		mockMvc.perform(delete("/kind/delete/{id}", 1))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).delete("1");
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void deleteFailureTest() throws Exception{
		when(kindService.delete("1")).thenReturn(false);
		mockMvc.perform(delete("/kind/delete/{id}", 1))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).delete("1");
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void deleteByNameContainingSuccessTest() throws Exception {
		String param=URLEncoder.encode("종류", "UTF-8");
		when(kindService.deleteByNameContaining(param)).thenReturn(true);
		mockMvc.perform(delete("/kind/deleteByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).deleteByNameContaining(param);
		verifyNoMoreInteractions(kindService);
	}

	@Test
	public void deleteByNameContainingFailureTest() throws Exception{
		String param=URLEncoder.encode("종류", "UTF-8");
		when(kindService.deleteByNameContaining(param)).thenReturn(false);
		mockMvc.perform(delete("/kind/deleteByNameContaining/{name}", param))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(kindService, times(1)).deleteByNameContaining(param);
		verifyNoMoreInteractions(kindService);
	}
}
