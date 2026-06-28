package com.usedcar.mapper;

import com.usedcar.domain.Listing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ListingMapper {
    int insert(Listing listing);
    int update(Listing listing);
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("publish") boolean publish);
    Listing findById(@Param("id") Long id);
    List<Listing> findBySeller(@Param("sellerId") Long sellerId);
    List<Listing> findNewestPublished(@Param("limit") int limit);
    List<Listing> findByIds(@Param("ids") List<Long> ids);

    void insertPhotos(@Param("listingId") Long listingId, @Param("urls") List<String> urls);
    void deletePhotos(@Param("listingId") Long listingId);
    List<String> findPhotoUrls(@Param("listingId") Long listingId);
}
