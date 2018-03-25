package net.kang.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Data
@Document(collection="agency")
public class Agency { // 기관. 본인이 직접 만듬.
	@Id
	String id;
	String name; // 기관 이름

	@DBRef(db="seongnam_city_park", lazy=false)
	Office office; // 시-구청
}
