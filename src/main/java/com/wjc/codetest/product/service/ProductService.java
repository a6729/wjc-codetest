package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        /**
         * 1. 문제: 코드 가독성
         * 2. 원인: 데이터 조회 후 처리 로직이 분리되어 있음
         * 3. 개선안: 데이터 조회 후 처리 로직이 분리되어 있어 불필요한 변수의 선언과 get() 메서드의 호출이 이루어집니다.
         *          또한 로직이 분리됨으로 인하여 가독성이 떨어지는 문제가 발생한다고 생각합니다.
         *          따라서 Optional 클래스에서 지원하는 orElseThrow() 메서드를 통해 데이터 조회 후 Product 타입의 변수를 바로 초기화하고,
         *          데이터가 조회되지 않는 경우에 바로 예외를 처리하도록 아래와 같이 개선하는 것이 좋을 것 같습니다.
         *
         *          <기존>
         *          Optional<Product> productOptional = productRepository.findById(productId);
         *          if (!productOptional.isPresent()) {
         *              throw new RuntimeException("product not found");
         *          }
         *
         *          <변경>
         *          Product product = productRepository.findById(productId)
         *                               .orElseThrow(() -> new RuntimeException("product not found"));
         *
         *          * 추가로, RuntimeException은 적합하지 않다고 생각합니다. 해당 내용은 throw 코드 부분에 작성하겠습니다.
         */
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            /**
             * 1. 문제: RestAPI 실행 결과에 적합하지 않은 응답
             * 2. 원인: 데이터가 조회되지 않은 경우 RuntimeException이 발생되도록 설계됨
             * 3. 개선안: 현재 로직은 id를 통해 조회한 데이터가 존재하지 않는 경우(null) RuntimeException을 클라이언트에 throw 하도록 되어 있습니다.
             *          RuntimeException이 발생하는 경우 GlobalExceptionHandler 클래스의 runTimeException() 메서드가 RestAPI의 응답을 담당하게 됩니다.
             *
             *          GlobalExceptionHandler.runTimeException() 메서드를 분석해보면 발생한 예외에 대한 로그를 남기고,
             *          클라이언트에는 HTTP Response Status를 500으로 응답하게 됩니다.
             *
             *          이 포인트에서 중요한 점은, 클라이언트에 HTTP Response Status를 500으로 응답하게 된다는 점입니다.
             *          해당 코드는 HTTP 표준 상 서버에서 요청을 처리하는 중 오류가 발생했다는 의미로 사용됩니다.
             *          하지만, 해당 조건은 데이터가 조회되지 않은 상황이기 때문에 500 코드를 응답하는 것은 부적합하다고 생각합니다.
             *
             *          따라서 RuntimeException이 아닌 NoSuchElementException와 같은 Exception을 throw 하도록 코드를 변경하고,
             *          GlobalExceptionHandler 클래스에 NoSuchElementException에 대한 처리를 담당하는 메서드를 구현하는 것이 좋을 것 같습니다.
             *          * 해당 메서드에는 기본적인 로깅과 클라이언트에 명확한 메세지를 응답해야 한다고 생각합니다. 또한 HTTP Response Status는 4xx로 작성해야 합니다.
             */
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    public Product update(UpdateProductRequest dto) {
        /**
         * 1. 문제: 의도를 명확하게 파악하기 어려운 코드
         * 2. 원인: 미흡한 캡슐화
         * 3. 개선안: 해당 예제 코드의 경우에는 로직과 비즈니스가 많이 단순하여 의도를 파악하기가 어렵지 않습니다.
         *          그러나 업데이트 외에도 다양한 로직이 추가되거나, Product 클래스에 더 많은 멤버변수(필드)가 존재한다면
         *          update() 메서드의 흐름을 파악하기 위해 많은 시간을 소모해야 할 수도 있습니다.
         *          * 만약, 상품의 변경 횟수를 추가하는 로직이 존재한다면 product.setUpdateCount(product.getUpdateCount() + 1); 과 같이
         *            읽기 위한 노력을 더 들여야 하는 코드들이 발생할 수도 있습니다.
         *
         *          이와 같은 문제를 해결하기 위해 Product의 업데이트를 위한 기능들을 하나의 메서드로 모으고
         *          그 역할을 Product 클래스가 가지도록 하여 클라이언트 코드인 ProductService.update() 메서드에서는
         *          1. JPA를 통한 DB 값 조회
         *          2. Product 클래스의 업데이트를 위한 메서드 호출
         *          3. 결과에 대한 리턴
         *          과 같이 로직을 리팩토링하는 것이 좋을 것 같습니다.
         *
         *          * 물론 Entity 클래스의 필드 값을 하나하나 접근하여 변경할 수 있는 setter 메서드에 대한 문제도 있습니다.
         *            해당 내용에 대해서는 Product 클래스에 작성해놓았습니다.
         */
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    public Page<Product> getListByCategory(GetProductListRequest dto) {
        /**
         * 1. 문제: 클라이언트 친화적이지 못한 RestAPI
         * 2. 원인: 예외적인 파라미터에 대한 처리 미흡
         * 3. 개선안: 해당 로직의 경우에는 제 생각에는 크게 2가지의 문제점이 존재한다고 생각합니다.
         *          1. Spring Data JPA의 Pageable의 page는 0부터 시작한다.
         *             (Front-End 코드가 존재하는 것이 아니라 일반적으로 많이 사용하는 오프셋 기반의 페이징으로 가정했습니다.)
         *
         *          2. 클라이언트(Front-End)에서 페이지 표시를 위한 데이터가 제공되지 않는다.
         *
         *          3. size 파라미터가 누락된 경우 RuntimeException이 발생하며, 클라이언트는 HTTP Response Status를 500으로 응답받게 된다.
         *
         *          첫 번째 문제는 UI/UX가 더 보기 방식인 경우에는 괜찮을 수 있으나
         *          일반적으로 많이 사용하는 오프셋 기반(1 2 3 4 5) 페이징 방식의 경우에는 페이지가 1부터 시작하게 되는데,
         *          Pageable의 경우에는 page가 0부터 시작되기 때문에 비즈니스 로직에서 논리에 맞게 값을 보정해주거나
         *          Pageable을 사용하지 않는 방향으로 구조를 변경하는 것이 좋을 것 같습니다.
         *
         *          두 번째 문제는 오프셋 기반 페이징인 경우에는 리스트 하단에 페이지를 표시하기 위한 데이터를 제공해야 합니다.
         *          만약 UI상 페이지가 10개까지 표시되는 경우(1 2 3 4 5 6 7 8 9 10)에도 데이터가 7페이지까지만 존재한다면 1~7까지의 페이지만 표시되어야 합니다.
         *          이것을 계산하여 표시하기 위해 API에서는 전체 데이터의 카운트(검색조건이 있다면 검색조건에 부합하는 카운트)를 함께 클라이언트에 응답해야 합니다.
         *
         *          세 번째 문제는 size 파라미터가 누락되었을 때, PageRequest.of() 메서드를 통해 페이징 처리를 위한 인스턴스를 생성할 때 IllegalArgumentException이 발생합니다.
         *          원인은 PageRequest.of() 메서드를 추적해보면 size의 값이 1보다 작으면 IllegalArgumentException을 throw 하도록 작성되어 있기 때문입니다.
         *          Java는 인스턴스가 생성될 때 멤버변수(필드) 중 int의 경우에는 0으로 초기화되는데 파라미터가 누락되면 size 변수의 값은 0으로 전달됩니다.
         *
         *          이 때문에 IllegalArgumentException이 발생하게 되고 해당 Exception은 RuntimeException을 상속받고 있어,
         *          GlobalExceptionHandler.runTimeException() 메서드가 예외 처리를 담당하여 클라이언트에 HTTP Response Status를 500으로 반환하게 됩니다.
         *
         *          하지만 파라미터가 누락됨으로 인해 응답코드가 500으로 반환되는 것은 HTTP 표준에 맞지 않는 설계입니다.
         *          따라서 size가 누락된 경우에는 기본 사이즈를 지정하여 이와 같은 예외가 발생하지 않도록 처리하거나
         *          파라미터가 누락된 경우에는 4xx 코드를 응답하도록 예외처리를 하는 것이 좋을 것 같습니다.
         */
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}