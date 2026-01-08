package com.wjc.codetest.product.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
/**
 * TODO : 피드백에 대한 말을 매끄럽게 작성하기 어려워 다른 부분 먼저 작성 후 차후에 작성 예정
 * 1. 문제: 무분별한 값의 변경 가능성과 비즈니스적 의미를 표현할 수 없음
 * 2. 원인: 클래스 레벨에 @Setter 어노테이션을 작성
 * 3. 개선안: 만약, setter() 메서드가 열려있는 경우 여러 비즈니스 로직에서 호출하여 사용할 수 있습니다.
 *          예를 들어 10개의 비즈니스 로직에서 직접 setName() 메서드를 호출하여 사용하고 있다고 가정했을 때
 *          그 로직 중 하나가 개발자의 실수로 인해 product.setName(" " + name); 과 같이 작성되어 값이 잘못 변경되는 포인트가 발생할 수 있습니다.
 *          이와 같은 경우 개발자가 10개의 로직을 직접 확인하여 문제가 되는 부분을 찾아야 하는 수고로움이 생기고
 *
 *          또한 만약 비즈니스의 요구사항이 변경되어 상품명을 변경하는 경우 '변경됨' 이라는 텍스트를 상품명 앞에 추가로 붙여야 하는 경우에
 *          setName() 메서드를 직접 정의하여 처리할 수 있지만, 메서드 명만 봤을 때는
 */
@Setter
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 1. 문제: 불필요한 코드의 반복
     * 2. 원인: @Column 어노테이션 작성
     * 3. 개선안: JPA의 경우에는, 클래스 레벨에 @Entity 어노테이션이 작성되어 있으면 해당 클래스를 DB의 테이블과 매핑하고
     *          클래스의 멤버변수(필드)를 테이블의 컬럼(필드)와 매핑합니다.
     *          매핑의 기준은 DB의 컬럼(필드)와 클래스의 멤버변수(필드)명이며,
     *          기본적으로 DB의 경우에는 UnderScore, Java의 경우에는 CamelCase로 매핑합니다.
     *          (예: 'DB(Table) > product_name' <-> 'Java(Class) -> productName')
     *
     *          위 과정은 @Column 어노테이션이 존재하지 않더라도 클래스 레벨에 @Entity 어노테이션이 작성된 경우 진행되기 때문에,
     *          필드에 특별한 제약이 존재하거나 특별히 DB의 컬럼(필드)명과 Java의 멤버변수(필드)명이 다른 등
     *          개발자가 직접 조정해야 하는 부분이 존재하는 것이 아니라면 생략하는 것을 권장합니다.
     *          (예제 코드는 단순하여 필드가 많지 않지만, 필드가 많은 경우에는 @Column 어노테이션이 추가됨으로써 읽기 불편한 코드가 될 수 있다고 생각합니다.)
     */
    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    /**
     * 1. 문제: 가독성 및 반복되는 코드 작성 가능성
     * 2. 원인: 기본 생성자 직접 작성
     * 3. 개선안: JPA의 경우에는 기본 생성자가 반드시 필요하고, 이와 같이 직접 기본 생성자를 작성하는 것이 잘못된 것은 아닙니다.
     *          그러나, 위에 말씀드린 것과 같이 필드가 많아지는 경우에는 이와 같은 코드가 개발자의 시선을 분산하여 읽기 불편하게 만들 수 있습니다.
     *
     *          또한 Product와 같은 Entity 클래스가 비즈니스 규모에 따라 현업에서는 수십 수백개 이상이 되는데,
     *          모든 코드에 직접 작성하는 것은 번거로운 부분이 되기도 하기에 Lombok 라이브러리의 @NoArgsConstructor 어노테이션을 이용하여
     *          클래스 레벨에 아래와 같이 작성하고 아래의 기본 생성자는 삭제하는 것을 권장합니다.
     *
     *          * 클래스 레벨에 작성 : @NoArgsConstructor(access = AccessLevel.PROTECTED)
     */
    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /**
     * 1. 문제: 불필요한 코드 작성
     * 2. 원인: getter 메서드 직접 작성
     * 3. 개선안: 클래스 레벨에 Lombok 라이브러리의 @Getter 어노테이션이 적용되어 있는 경우
     *          해당 클래스의 모든 멤버변수(필드)에 대한 getter 메서드가 컴파일 단계에서 자동으로 생성됩니다.
     *          그에 따라 아래의 getCategory() 및 getName() 메서드는 불필요한 코드이므로, 삭제하는 것을 권장합니다.
     */
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
