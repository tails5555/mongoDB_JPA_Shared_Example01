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
import net.kang.controller.OfficeController;
import net.kang.domain.Agency;
import net.kang.domain.Office;
import net.kang.service.OfficeService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {JUnitConfig.class, MongoConfig.class})
@WebAppConfiguration
public class OfficeControllerTest {
	static final int OFFICE_QTY=5;
	static final int AGENCY_QTY=10;
	MockMvc mockMvc;
	@Mock OfficeService officeService;
	@InjectMocks OfficeController officeController;

	private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

	private List<Office> officeList(){
		List<Office> officeList=new ArrayList<Office>();
		for(int k=0;k<OFFICE_QTY;k++) {
			Office office=new Office();
			office.setId(String.format("%d", k+1));
			office.setName(String.format("시구청%02d", k));
			office.setAddress(String.format("주소%02d", k));
			office.setHomepage(String.format("홈페이지%02d", k));
			office.setZipCode(String.format("우편번호%02d", k));
			officeList.add(office);
		}
		return officeList;
	}

	private Office findOneOffice() {
		Office office=new Office();
		office.setId("1");
		office.setName("시구청01");
		office.setAddress("주소01");
		office.setZipCode("우편번호01");
		office.setHomepage("홈페이지01");
		return office;
	}

	private List<Agency> agencyList(Office tmpOffice){
		List<Agency> agencyList=new ArrayList<Agency>();
		for(int k=0;k<AGENCY_QTY;k++) {
			Agency agency=new Agency();
			agency.setId(String.format("%d", k+1));
			agency.setName(String.format("기관%02d", k));
			agency.setOffice(tmpOffice);
			agencyList.add(agency);
		}
		return agencyList;
	}

	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(officeController).build();
	}

	@Test
	public void findAllSuccessTest() throws Exception{
		List<Office> tmpList=officeList();
		when(officeService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/office/findAll"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findAll();
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findAllFailureTest() throws Exception{
		List<Office> tmpList=new ArrayList<Office>();
		when(officeService.findAll()).thenReturn(tmpList);
		String toJSON=this.jsonStringFromObject(tmpList);
		mockMvc.perform(get("/office/findAll"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findAll();
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findOneSuccessTest() throws Exception{
		Optional<Office> tmpResult=Optional.of(findOneOffice());
		when(officeService.findOne("1")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/office/findOne/{id}", 1))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findOne("1");
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findOneFailureTest() throws Exception{
		Optional<Office> tmpResult=Optional.of(new Office());
		when(officeService.findOne("1")).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult.get());
		mockMvc.perform(get("/office/findOne/{id}", 1))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findOne("1");
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findOneAndAgencyFindAllSuccessTest() throws Exception{
		Office tmpOffice=findOneOffice();
		List<Agency> tmpAgencyList=agencyList(tmpOffice);
		String toJSON=this.jsonStringFromObject(tmpAgencyList);
		when(officeService.findOneAndAgencyFindAll(tmpOffice.getId())).thenReturn(tmpAgencyList);
		mockMvc.perform(get("/office/findOne/agencyList/{id}", "1"))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findOneAndAgencyFindAll("1");
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findOneAndAgencyFindAllFailureTest() throws Exception{
		List<Agency> tmpAgencyList=new ArrayList<Agency>();
		String toJSON=this.jsonStringFromObject(tmpAgencyList);
		when(officeService.findOneAndAgencyFindAll("1")).thenReturn(tmpAgencyList);
		mockMvc.perform(get("/office/findOne/agencyList/{id}", "1"))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findOneAndAgencyFindAll("1");
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findByNameContainingSuccessTest() throws Exception{
		List<Office> tmpResult=officeList();
		String param=URLEncoder.encode("시구청", "UTF-8");
		when(officeService.findByNameContaining(param)).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult);
		mockMvc.perform(get("/office/findByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void findByNameContainingFailureTest() throws Exception{
		List<Office> tmpResult=new ArrayList<Office>();
		String param=URLEncoder.encode("시구청", "UTF-8");
		when(officeService.findByNameContaining(param)).thenReturn(tmpResult);
		String toJSON=this.jsonStringFromObject(tmpResult);
		mockMvc.perform(get("/office/findByNameContaining/{name}", param))
		.andExpect(status().isNoContent())
		.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
		.andExpect(content().string(equalTo(toJSON)))
		.andDo(print());

		verify(officeService, times(1)).findByNameContaining(param);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void insertSuccessTest() throws Exception{
		Office office=findOneOffice();
		when(officeService.insert(office)).thenReturn(true);
		String requestOffice=this.jsonStringFromObject(office);

		mockMvc.perform(
				post("/office/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestOffice))
		.andExpect(status().isCreated())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).insert(office);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void insertFailureTest() throws Exception{
		Office office=findOneOffice();
		when(officeService.insert(office)).thenReturn(false);
		String requestOffice=this.jsonStringFromObject(office);

		mockMvc.perform(
				post("/office/insert")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestOffice))
		.andExpect(status().isConflict())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).insert(office);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void updateSuccessTest() throws Exception{
		Office office=findOneOffice();
		when(officeService.update(office)).thenReturn(true);
		String requestOffice=this.jsonStringFromObject(office);

		mockMvc.perform(
				put("/office/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestOffice))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).update(office);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void updateFailureTest() throws Exception{
		Office office=findOneOffice();
		when(officeService.update(office)).thenReturn(false);
		String requestOffice=this.jsonStringFromObject(office);

		mockMvc.perform(
				put("/office/update")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(requestOffice))
		.andExpect(status().isNotModified())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).update(office);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void deleteSuccessTest() throws Exception{
		when(officeService.delete("1")).thenReturn(true);

		mockMvc.perform(delete("/office/delete/{id}", 1))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).delete("1");
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void deleteFailureTest() throws Exception{
		when(officeService.delete("1")).thenReturn(false);

		mockMvc.perform(delete("/office/delete/{id}", 1))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).delete("1");
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void deleteByNameContainingSuccessTest() throws Exception{
		String param=URLEncoder.encode("시구청", "UTF-8");
		when(officeService.deleteByNameContaining(param)).thenReturn(true);

		mockMvc.perform(delete("/office/deleteByNameContaining/{name}", param))
		.andExpect(status().isOk())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).deleteByNameContaining(param);
		verifyNoMoreInteractions(officeService);
	}

	@Test
	public void deleteByNameContainingFailureTest() throws Exception{
		String param=URLEncoder.encode("시구청", "UTF-8");
		when(officeService.deleteByNameContaining(param)).thenReturn(false);

		mockMvc.perform(delete("/office/deleteByNameContaining/{name}", param))
		.andExpect(status().isNotFound())
		.andDo(print()).andReturn();

		verify(officeService, times(1)).deleteByNameContaining(param);
		verifyNoMoreInteractions(officeService);
	}
}
