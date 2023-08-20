package com.spring.shopping.service;

import com.spring.shopping.DTO.ProductDetailResponseDTO;
import com.spring.shopping.DTO.ProductSaveRequestDTO;
import com.spring.shopping.entity.Product;
import com.spring.shopping.entity.ProductImage;
import com.spring.shopping.entity.ShopCategory;
import com.spring.shopping.exception.CategoryIdNotFoundException;
import com.spring.shopping.exception.ProductIdNotFoundException;
import com.spring.shopping.repository.ProductImageRepository;
import com.spring.shopping.repository.ProductRepository;
import com.spring.shopping.repository.ShopCategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ShopCategoryRepository shopCategoryRepository;

    final int PAGE_SIZE = 20; // 한 페이지에 몇 개의 상품을 조회할지

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              ProductImageRepository productImageRepository,
                              ShopCategoryRepository shopCategoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.shopCategoryRepository = shopCategoryRepository;
    }


    // 전체 조회 - 디폴트 정렬 : productId 기준 내림차순
    @Override
    public Page<Product> getAllProducts(int pageNum) {
        Page<Product> allProducts = productRepository.findAll(PageRequest.of(
                pageNum - 1, PAGE_SIZE, Sort.Direction.DESC, "productId")
        );

        if (allProducts.getTotalPages() < pageNum) {  // 만약 페이지 번호가 범위를 벗어나는 경우에는 가장 마지막 페이지로 자동으로 이동
            return productRepository.findAll(
                    PageRequest.of(allProducts.getTotalPages() - 1, PAGE_SIZE, Sort.Direction.DESC, "productId"));
        } else {
            return allProducts;
        }
    }

    // 카테고리별 조회 - 디폴트 정렬 : productId 기준 내림차순
    @Override
    public Page<Product> getProductsByCategory(Long categoryId, int pageNum) {
        Page<Product> productsByCategory = productRepository.findByShopCategoryCategoryId(
                categoryId,
                PageRequest.of(pageNum - 1, PAGE_SIZE, Sort.Direction.DESC, "productId")
        );

        if (productsByCategory.isEmpty()) {
            throw new CategoryIdNotFoundException("존재하지 않는 카테고리입니다");
        }

        if (productsByCategory.getTotalPages() < pageNum) {
            return productRepository.findByShopCategoryCategoryId(
                    categoryId,
                    PageRequest.of(productsByCategory.getTotalPages() - 1, PAGE_SIZE, Sort.Direction.DESC, "productId")
            );
        } else {
            return productsByCategory;
        }
    }

    // 상품 상세 조회
    @Override
    public ProductDetailResponseDTO getProductDetailById(Long productId) {
        Product product = productRepository.findByProductId(productId);

        if (product == null) {
            throw new ProductIdNotFoundException("존재하지 않는 상품 ID입니다");
        }

        return ProductDetailResponseDTO.from(product);
    }




    // 검색 - 상품명으로 검색, 디폴트 정렬 : productId 기준 내림차순
    @Override
    public Page<Product> searchProductsByKeyword(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, PAGE_SIZE, Sort.Direction.DESC, "productId");
        Page<Product> searchResults = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);

        if (searchResults.getTotalElements() == 0) {
            // 만약 검색 결과가 0일 경우 빈 페이지를 생성하여 반환
            return Page.empty(pageable);
        }

        if (searchResults.getTotalPages() < pageNum) { // 만약 페이지 번호가 범위를 벗어나는 경우에는 가장 마지막 페이지로 자동으로 이동
            pageable = PageRequest.of(searchResults.getTotalPages() - 1, PAGE_SIZE, Sort.Direction.DESC, "productId");
            searchResults = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        }

        return searchResults;
    }


    @Override
    public boolean saveProductAndImage(ProductSaveRequestDTO requestDTO) {
        try {
            // 상품 정보 추출
            ShopCategory category = shopCategoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 categoryId"));

            Product newProduct = Product.builder()
                    .shopCategory(category)
                    .productName(requestDTO.getProductName())
                    .thumbnailUrl(requestDTO.getThumbnailUrl())
                    .price(requestDTO.getPrice())
                    .stockQuantity(requestDTO.getStockQuantity())
                    .build();

            Product savedProduct = productRepository.save(newProduct);

            // 이미지 정보 추출 및 저장
            List<ProductImage> productImages = requestDTO.getProductImageUrls().stream()
                    .map(imageUrl -> ProductImage.builder()
                            .product(savedProduct)
                            .imageUrl(imageUrl)
                            .build())
                    .collect(Collectors.toList());

            productImageRepository.saveAll(productImages);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
      // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @Override
    public Product getProductInfo(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
}