package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
/**
 * ** 아래 Mapping 별 피드백을 적용한 것을 가정한 피드백입니다. **
 * 1. 문제: 중복코드 발생
 * 2. 원인: @RequestMapping에 Path 누락
 * 3. 개선안: 해당 Controller 클래스의 Mapping들의 Path에서 /product~ 부분이 중복됩니다.
 *          따라서 아래 클래스 레벨의 @RequestMapping의 속성을 아래와 같이 추가하고,
 *          메서드 레벨의 Mapping(@GetMapping, @PostMapping 등)들의 /product 부분을 제거하는 것이 좋을 것 같습니다.
 *          [클래스 레벨] @RequestMapping -> @RequestMapping(value = "/product")
 *          [메서드 레벨] @GetMapping(value = "/product/by/{productId}") -> @GetMapping(value = "/by/{productId}")
 *                     @PostMapping(value = "/product") -> @PostMapping
 *                     ...
 */
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService; // 해당 라인 위에 빈 라인을 추가하여, 가독성을 높이는 것을 권장합니다.

    /**
     * 1. 문제: RestAPI 설계 원칙 미준수
     * 2. 원인: URI에 행위가 포함됨
     * 3. 개선안: HTTP 표준 스펙에 근거하여 URI에는 최대한 자원에 대한 정보만 작성하고,
     *          행위(GET, POST, DELETE, PUT 등)은 HTTP Method를 이용하는 것을 권장합니다.
     *
     *          따라서 URI(URL)를 아래와 같이 변경하는 것이 좋을 것 같습니다.
     *          기존 : GET /get/product/by/{productId}
     *          변경 : GET /product/by/{productId}
     */
    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /**
     * 1. 문제: RestAPI 설계 원칙 미준수
     * 2. 원인: URI에 행위가 포함됨
     * 3. 개선안: HTTP 표준 스펙에 근거하여 URI에는 최대한 자원에 대한 정보만 작성하고,
     *          행위(GET, POST, DELETE, PUT 등)은 HTTP Method를 이용하는 것을 권장합니다.
     *
     *          따라서 URI(URL)를 아래와 같이 변경하는 것이 좋을 것 같습니다.
     *          기존 : POST /create/product
     *          변경 : POST /product
     */
    @PostMapping(value = "/create/product")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest dto){
        Product product = productService.create(dto);
        /**
         * 1. 문제: 보안적 이슈와 의도치 않은 API의 스펙 변동 가능성
         * 2. 원인: HTTP Response Body에 Entity 인스턴스를 응답
         * 3. 개선안: productService.create(); 메서드의 로직을 보면 JPA를 통해 DB에 값을 저장하고 그 리턴 값을 그대로 반환하고 있습니다.
         *          이 예제코드(Product)의 경우에는 민감한 데이터가 포함되어 있지 않지만, 클라이언트에 노출되면 안되는 필드/데이터가 존재할 수 있습니다.
         *
         *          또한 Entity 클래스는 DB와 밀접한 연관이 있습니다.
         *          실제로 DB에 필드가 추가되는 경우 Entity에도 매핑을 진행하기 위해 멤버변수(필드)를 추가하게 됩니다.
         *          그러나 추가되는 필드의 경우 비즈니스와 상관 없는 경우가 존재할 수도 있습니다.
         *          그런 경우 필요하지 않은 경우에도 RestAPI의 응답 스펙이 변경된다고 생각합니다.
         *
         *          위와 같은 근거를 바탕으로 새로운 데이터를 생성(create)하는 경우에는 HTTP Status를 200(OK) 또는 201(Created)로 지정하고,
         *          HTTP Response Body에 생성된 데이터를 조회할 수 있는 키(id, product_id)를 전달하거나
         *          필요한 데이터만 선별하여 별도의 VO 클래스를 생성하여 HTTP Response Body에 전달하는 것이 좋을 것 같습니다.
         */
        return ResponseEntity.ok(product);
    }

    /**
     * 1. 문제: RestAPI 설계 원칙 미준수
     * 2. 원인: URI에 행위가 포함됨
     * 3. 개선안: HTTP 표준 스펙에 근거하여 URI에는 최대한 자원에 대한 정보만 작성하고,
     *          행위(GET, POST, DELETE, PUT 등)은 HTTP Method를 이용하는 것을 권장합니다.
     *
     *          따라서 URI(URL)와 HTTP Method를 아래와 같이 변경하는 것이 좋을 것 같습니다.
     *          기존 : POST /delete/product/{productId}
     *          변경 : DELETE /product/{productId}
     */
    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    /**
     * 1. 문제: RestAPI 설계 원칙 미준수
     * 2. 원인: URI에 행위가 포함됨
     * 3. 개선안: HTTP 표준 스펙에 근거하여 URI에는 최대한 자원에 대한 정보만 작성하고,
     *          행위(GET, POST, DELETE, PUT 등)은 HTTP Method를 이용하는 것을 권장합니다.
     *          또한, 특정한 자원에 대한 변경(DELETE, UPDATE 등)이 이루어지는 경우에는 URI(URL)에 명시하는 것을 권장합니다.
     *
     *          따라서 URI(URL)와 HTTP Method를 아래와 같이 변경하는 것이 좋을 것 같습니다.
     *          기존 : POST /update/product
     *          변경 : PATCH /product/{productId} 또는 POST /product/{productId}
     */
    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequest dto){
        Product product = productService.update(dto);
        /**
         * 1. 문제: 보안적 이슈와 의도치 않은 API의 스펙 변동 가능성
         * 2. 원인: HTTP Response Body에 Entity 인스턴스를 응답
         * 3. 개선안: productService.update(); 메서드의 로직을 보면 JPA를 통해 DB에 값을 업데이트하고 그 리턴 값을 그대로 반환하고 있습니다.
         *          이 예제코드(Product)의 경우에는 민감한 데이터가 포함되어 있지 않지만, 클라이언트에 노출되면 안되는 필드/데이터가 존재할 수 있습니다.
         *
         *          또한 Entity 클래스는 DB와 밀접한 연관이 있습니다.
         *          실제로 DB에 필드가 추가되는 경우 Entity에도 매핑을 진행하기 위해 멤버변수(필드)를 추가하게 됩니다.
         *          그러나 추가되는 필드의 경우 비즈니스와 상관 없는 경우가 존재할 수도 있습니다.
         *          그런 경우 필요하지 않은 경우에도 RestAPI의 응답 스펙이 변경된다고 생각합니다.
         *
         *          위와 같은 근거를 바탕으로 데이터를 업데이트하는 경우에는 HTTP Status를 200(OK)으로 지정하고,
         *          HTTP Response Body에 업데이트 된 데이터를 조회할 수 있는 키(id, product_id)를 전달하거나
         *          필요한 데이터만 선별하여 별도의 VO 클래스를 생성하여 HTTP Response Body에 전달하는 것이 좋을 것 같습니다.
         */
        return ResponseEntity.ok(product);
    }

    /**
     * 1. 문제: RestAPI 설계 원칙 미준수
     * 2. 원인: 행위에 맞지 않는 HTTP Method 지정
     * 3. 개선안: HTTP 표준 스펙에 근거하여 URI에는 최대한 자원에 대한 정보만 작성하고,
     *          행위(GET, POST, DELETE, PUT 등)은 HTTP Method를 이용하는 것을 권장합니다.
     *
     *          그러나 아래의 코드는 카테고리를 기준으로 상품을 조회하는 RestAPI로 보이나 HTTP Method가 POST로 지정되어 있습니다.
     *          따라서 HTTP Method를 아래와 같이 변경하는 것이 좋을 것 같습니다.
     *          기존 : POST /product/list
     *          변경 : GET /product/list
     *
     *          추가로, GET 방식의 Request인 경우에도 HTTP Body를 전송하지 못하는 것은 아니나
     *          HTTP 표준 상 query string으로 전송하는 것이 더 적합한 방식입니다.
     *          따라서 @RequestBody 어노테이션을 제거하여 Spring MVC에서 query string의 파라미터를 매핑할 수 있도록 변경하는 것이 좋을 것 같습니다.
     */
    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(@RequestBody GetProductListRequest dto){
        Page<Product> productList = productService.getListByCategory(dto);
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}