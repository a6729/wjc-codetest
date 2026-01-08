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
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    public Product update(UpdateProductRequest dto) {
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
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}