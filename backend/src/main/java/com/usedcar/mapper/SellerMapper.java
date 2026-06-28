package com.usedcar.mapper;

import com.usedcar.domain.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SellerMapper {
    int insert(Seller seller);
    Seller findByEmail(@Param("email") String email);
    Seller findById(@Param("id") Long id);
}
