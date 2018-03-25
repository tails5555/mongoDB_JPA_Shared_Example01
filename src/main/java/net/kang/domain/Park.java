package net.kang.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;
import net.kang.model.Position;

@Data
@Document(collection="park")
public class Park { // 공원 데이터
	@Id
	String id;

	String manageNo; // 관리 번호
	String name; // 공원 이름

	@DBRef(db="seongnam_city_park", lazy=false)
	Kind kind; // 공원 종류

	String oldAddress; // 지번주소
	String newAddress; // 도로명주소

	Position position; // 위치(위도, 경도)
	double area; // 면적

	List<String> jymFacility; // 운동시설
	List<String> playFacility; // 유희시설
	List<String> convFacility; // 편의시설
	List<String> cultFacility; // 교양시설
	List<String> anotFacility; // 기타시설

	@DateTimeFormat(iso = ISO.DATE_TIME)
	Date designateDate; // 지정고시일

	@DBRef(db="seongnam_city_park", lazy=false)
	Agency agency; // 기관

	String callPhone; // 연락처
}
