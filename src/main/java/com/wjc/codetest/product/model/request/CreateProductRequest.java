package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 * 1. 문제: 해당 클래스를 통한 Request를 받는 경우 오류 발생
 * 2. 원인: 기본 생성자가 존재하지 않음
 * 3. 개선안: HTTP Request가 서버로 전달되면 Spring MVC는 1차적으로 요청을 처리할 Controller 메서드를 찾습니다.
 *          이후 메서드의 매개변수에 @RequestBody 어노테이션이 존재하는 경우 Jackson 라이브러리를 통해 Request Body의 데이터를 역직렬화 합니다.
 *          Jackson 라이브러리의 경우, 클래스의 기본 생성자와 필드명을 기준으로 값을 직접 주입하여 역직렬화를 처리합니다.
 *
 *          따라서, CreateProductRequest 클래스를 통하여 Request Body의 데이터를 받기 위해서는 기본 생성자가 필요합니다.
 *          Lombok 라이브러리의 @NoArgsConstructor 어노테이션을 작성하여 해당 클래스의 역할을 수행할 수 있도록 해야합니다.
 */
public class CreateProductRequest {
    private String category;
    private String name;

    public CreateProductRequest(String category) {
        this.category = category;
    }

    public CreateProductRequest(String category, String name) {
        this.category = category;
        this.name = name;
    }
}

