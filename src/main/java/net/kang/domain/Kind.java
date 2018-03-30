package net.kang.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="kind")
@CompoundIndexes({
    @CompoundIndex(name = "name_unique", def = "{'name' : 1}", unique = true)
}) // 공원 종류도 마찬가지로 이름으로 findOne이 가능하게끔 하기 위해서 인덱스를 작성함.
public class Kind { // 공원 종류. 본인이 새로 만들었음.
	@Id
	String id;
	String name; // 공원 종류 이름
}
