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
})
public class Kind {
	@Id
	String id;
	String name; // 공원 종류 이름
}
