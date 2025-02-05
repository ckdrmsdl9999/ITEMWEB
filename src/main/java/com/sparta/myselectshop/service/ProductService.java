package com.sparta.myselectshop.service;

import com.sparta.myselectshop.dto.ProductMypriceRequestDto;
import com.sparta.myselectshop.dto.ProductRequestDto;
import com.sparta.myselectshop.dto.ProductResponseDto;
import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import com.sparta.myselectshop.entity.UserRoleEnum;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    public static final int MIN_MY_PRICE = 100;

    private final ProductRepository productRepository;


    public ProductResponseDto createProduct(ProductRequestDto requestDto, User user) {
        Product product = productRepository.save(new Product(requestDto, user));//ctrl+b 생성자이동
        return new ProductResponseDto(product);
    }

    @Transactional//dirty checking(변경감지)
    public ProductResponseDto updateProduct(Long id, ProductMypriceRequestDto requestDto) {
        int myprice = requestDto.getMyprice();
        if(myprice<MIN_MY_PRICE){//BEAN VALIDATION으로 제어해도좋음
            throw new IllegalArgumentException("유효하지 않은 관심가격이빈다 최소 "+MIN_MY_PRICE+
                    "원 이상으로 설정해주세요");
        }

        Product product=productRepository.findById(id).orElseThrow(()
                ->new NullPointerException("해당상품을 찾을 수 없습니다"));

        product.update(requestDto);
        return new ProductResponseDto(product);
    }

    public Page<ProductResponseDto> getProducts(User user, int page, int size, String sortBy, boolean isAsc) {//함수형으로 한줄로해보기

        Sort.Direction direction=isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        UserRoleEnum userRoleEnum = user.getRole();
        Page<Product> productList;
        if(userRoleEnum==userRoleEnum.USER){
            productList=productRepository.findAllByUser(user,pageable);
        }else {
            productList=productRepository.findAll(pageable);
        }

//        List<ProductResponseDto> responseDtoList=new ArrayList<>();
//        List<Product> products=productRepository.findAllByUser( user); //이것도 altenter되네
//
//        for (Product product : products) {
//            responseDtoList.add(new ProductResponseDto(product));
//
//        }
        return productList.map(ProductResponseDto::new);

    }
    @Transactional
    public void updateBySearch(Long id, ItemDto itemDto) {
    Product product = productRepository.findById(id).orElseThrow(()->new NullPointerException("해당" +
    "상품은 존재하지 않습니다")
    );
     product.updateByItemDto(itemDto);
    }

//    public List<ProductResponseDto> getAllProducts() { //페이징추가하면 또만들어야되서
//        List<ProductResponseDto> responseDtoList=new ArrayList<>();
//        List<Product> products=productRepository.findAll(); //이것도 altenter되네
//
//        for (Product product : products) {
//            responseDtoList.add(new ProductResponseDto(product));
//
//        }
//        return responseDtoList;
//    }
}
