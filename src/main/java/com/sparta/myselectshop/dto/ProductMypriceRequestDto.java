package com.sparta.myselectshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductMypriceRequestDto {//여기다 BEAN VALIDATION해도좋음 해봬자 100이상이런거
    private int myprice;
}