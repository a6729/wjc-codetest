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