# mongoDB_JPA_Shared_Example01
MongoDB 데이터베이스에 실제로 공공데이터에서 제공하는 엑셀 데이터를 저장해보는 예제

## Issues
- MongoDB에서 관계성을 적용시키는 @DBRef를 이용하는 연습을 진행합니다.
- Mock MVC를 직접 형성하여 관계형 데이터베이스에 대해 보장된 Service, Controller 클래스에 대해 단위 테스팅을 할 수 있도록 보장을 합니다.
- Spring Data MongoDB를 기반으로 공공데이터 Excel 파일을 입력 받아서 REST API로 송출하는 연습합니다.

## Study Docs
스터디 자료는 현재 프로젝트의 `src > doc` 파일에 PDF 파일로 제공을 하였습니다.
 
스터디 자료는 향시에 수정이 될 수 있으니 이 점 참고하시길 바라겠습니다.

[스터디 자료 참고하기](https://github.com/tails5555/mongoDB_JPA_Shared_Example01/blob/master/src/doc/MongoDB%2BSpringJPA_03_Document_Relationship.pdf)

## Maven pom.xml
`pom.xml`를 기반으로 Maven Dependency를 구성하여 Update Maven은 필수입니다.

```
<dependencies>
	<!-- 1. Spring Data JPA Starter -->
	<!-- 이는 실제로 RDBMS에서 하게 된다면 필요하지만, MongoDB Data에서는 MongoRepository를 따로 제공한다. -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-jpa</artifactId>
	</dependency>
	<!-- 2. Spring Data MongoDB Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-mongodb</artifactId>
	</dependency>
	<!-- 3. Spring Web MVC Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
	</dependency>
	<!-- 4. Lombok Project -->
	<!-- Lombok은 각 인스턴스들에 대해서 getter, setter, toString, equals, hashCode 등의 구현을 자동으로 해 주는 프로젝트이다. -->
	<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
		<optional>true</optional>
	</dependency>
	<!-- 5. Tomcat Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-tomcat</artifactId>
		<scope>provided</scope>
	</dependency>
	<!-- 6. Spring Test Starter -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
	<!-- 7. Apache POI -->
	<!-- Excel 파일을 받아오기 위해 쓴 Library인데 확장자가 xls이면 HSSF를 이용해야 한다.-->
	<dependency>
		<groupId>org.apache.poi</groupId>
		<artifactId>poi</artifactId>
		<version>3.17</version>
	</dependency>
	<!-- 7. MongoDB JDBC Driver -->
	<dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongo-java-driver</artifactId>
        <version>3.6.3</version>
    </dependency>
</dependencies>
```

## References
- 공공데이터 엑셀 파일은 성남시 공원 목록 정보를 이용했습니다.
- [성남공공데이터넷](http://data.seongnam.go.kr/main.do)
- 데이터 제공 페이지 : [접속하기](http://data.seongnam.go.kr/open/SHEET/view.do?menu_cd=MENU_1_00&rid=248)

<img alt="저작권표시" src="http://data.seongnam.go.kr/images/user/kogl_mark.jpg" width="100" height="38">

## Output Style
공원 데이터의 형식은 아래와 같이 출력이 됩니다.

```
{
    "id": "5ac0757ac99ff9042489669d",
    "manageNo": "41131-00003",
    "name": "영장공원",
    "kind": {
        "id": "5ab5fc2a4421072258a1bd33",
        "name": "근린공원"
    },
    "oldAddress": "경기도 성남시 수정구 태평동 산50-1",
    "newAddress": "",
    "position": {
        "posX": 37.4500712,
        "posY": 127.1338562
    },
    "area": 538022,
    "jymFacility": [
        "게이트볼장",
        "농구장"
    ],
    "playFacility": [
        "어린이놀이터"
    ],
    "convFacility": [
        "화장실",
        "주차장",
        "약수터"
    ],
    "cultFacility": [
        "임간학습장",
        "자생초화원"
    ],
    "anotFacility": [
        ""
    ],
    "designateDate": "1972-11-02T15:00:00.000+0000",
    "agency": {
        "id": "5ab5fd8e4421072258a1bd3c",
        "name": "경기도 성남시 공원과",
        "office": {
            "id": "5abafca45870f22c1829be51",
            "name": "성남시청",
            "homepage": "http://www.seongnam.go.kr/index.do",
            "zipCode": "13437",
            "address": "경기도 성남시 중원구 성남대로 997(여수동)"
        }
    },
    "callPhone": "031-729-4272"
}
```

> - id : MongoDB를 기반으로 작성한 Park의 _id. Primary Key 역할을 합니다.
> - manageNo : 각 공원 별 관리 번호. 이는 유일한 값입니다.
> - name : 공원 이름. 공원 이름은 아직 지어지지 않은 공원에 대해서 이름을 Null로 둘 수도 있습니다.
> - kind : 공원 종류. 공원 종류는 데이터베이스에서 ENUM을 기반으로 정리한 값들 중 하나를 이용합니다.
> - oldAddress : 지번 주소. 대부분 공원 데이터는 지번 주소로 저장 되어 있는 경우가 많습니다.
> - newAddress : 도로명 주소. 도로명 주소는 생각보다 많이 바뀔 수 있어서 빈 값도 존재합니다.
> - position : posX, posY를 각각 위도, 경도를 저장해서 위치에 대한 Embedded Document로 사용합니다.
> - area : 공원 면적. 면적 단위는 제곱 미터(m2)로 작성합니다.
> - jymFacility : 운동, 헬스 시설. 각 운동 시설 별 종류를 배열로 저장합니다.
> - playFacility : 유희, 놀이 시설. 마찬가지로 유희 시설 별 종류를 배열로 저장합니다.
> - convFacility : 편의 시설. 마찬가지로 편의 시설 별 종류를 배열로 저장합니다.
> - cultFacility : 문화 시설. 마찬가지로 문화 시설 별 종류를 배열로 저장합니다.
> - anotFacility : 이외 시설. 마찬가지로 이외 시설 별 종류를 배열로 저장합니다.
> - designateDate : 지정 고시일. 이는 Java의 Date 타입으로 받되 ISO의 표준에 맞춰서 저장을 하도록 합니다.
> - agency : 시청 내에 있는 공원 관리 부서. Agency 배열에는 관리 부서와 담당 시-구청(Office)를 저장을 합니다.
> - callPhone : 공원 연락처. 공원 내에 있는 사무소 연락처를 저장합니다.
 
## Screenshot
![shared_example_01_result01](/src/doc/shared_example_01_result01.png "shared_example_01_result01")

REST API를 기반으로 한 결과는 위의 사진처럼 결과가 올바르게 나옵니다. 이는 Postman을 이용해서 작동을 했습니다.

![shared_example_01_result02](/src/doc/shared_example_01_result02.png "shared_example_01_result02")

JUnit Test와 Mockito Mock MVC를 기반으로 한 테스팅 결과입니다. Repository, Service, Controller 단위 별로 정상적으로 작동하는 것을 확인할 수 있습니다.

## Author
- [강인성](https://github.com/tails5555)