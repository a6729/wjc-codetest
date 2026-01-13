package com.wjc.codetest.product.repository;

import com.wjc.codetest.product.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 1. 문제: 코드에 대한 가독성이 떨어짐
     * 2. 원인: 실제 검색조건이 되는 필드명과, 메서드의 매개변수명이 다름
     * 3. 개선안: 아래의 메서드 명으로 작성하게 되면, 실제로는 아래와 같은 쿼리가 실행되게 됩니다.
     *          (SELECT * FROM product WHERE category = {category};
     *
     *          실제 쿼리의 조건문은 category 컬럼(필드)를 기준으로 조회하게 되지만,
     *          해당 메서드의 매개변수 명은 name이기에 단순한 부분이지만 가독성을 해치게 될 수 있고
     *          더 나아가서는 다른 개발자가 아래 코드만 보고 조회 조건을 잘못된 필드로 인지하고 진행하게 될 가능성도 있다고 생각합니다.
     *
     *          따라서 메서드의 매개변수명을 category로 변경하는 것이 좋을 것 같습니다.
     *          * Page<Product> findAllByCategory(String *category*, Pageable pageable);
     */
    Page<Product> findAllByCategory(String name, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}
