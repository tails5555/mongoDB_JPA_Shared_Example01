package net.kang.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
@Data
@Document(collection="agency")
@CompoundIndexes({
    @CompoundIndex(name = "name_unique", def = "{'name' : 1}", unique = true)
}) // 기관 이름을 유일하게 설정을 해서 findOne이 가능하게끔 하기 위해 이를 생성.
public class Agency { // 기관. 본인이 직접 만듬.
	@Id
	String id;
	String name; // 기관 이름

	@DBRef(db="seongnam_city_park", lazy=false)
	Office office; // 시-구청
}
