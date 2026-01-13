package com.wjc.codetest.product.model.response;

import com.wjc.codetest.product.model.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author : 변영우 byw1666@wjcompass.com
 * @since : 2025-10-27
 */
@Getter
@Setter
/**
 * 1. 문제: 의도치 않은 응답 데이터 변질 가능성
 * 2. 원인: ProductListResponse 클래스의 멤버변수(필드)에 final 키워드 누락
 * 3. 개선안: ProductListResponse 클래스는 클래스의 명칭과 ProductController 코드로 미루어 보았을 때,
 *          상품 목록을 조회하는 RestAPI의 응답 데이터를 전송하기 위한 클래스로 판단됩니다.
 *
 *          응답 데이터의 경우에는 한 번 데이터가 세팅되고 나면 그 이후로는 변경되지 않아야 한다고 생각합니다.
 *          응답은 결과이기 때문에, 논리적으로도 변경되지 않아야 한다고 생각하고 추가로 변경이 허용될 경우 예상치 못한 오류 포인트가 발생할 수 있습니다.
 *
 *          따라서 모든 멤버변수(필드)에 final 키워드를 적용하거나, Record를 적용하여 값이 불변하는 클래스로 설계를 변경하는 것이 좋을 것 같습니다.
 *          * 위와 같이 적용하게 되면 현재 클래스 레벨에 작성되어 있는 @Setter 어노테이션은 사용할 수 없으므로, 함께 제거해야 합니다.
 */
public class ProductListResponse {
    private List<Product> products;
    private int totalPages;
    private long totalElements;
    private int page;

    public ProductListResponse(List<Product> content, int totalPages, long totalElements, int number) {
        this.products = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.page = number;
    }
}
