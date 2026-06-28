package com.usedcar.search.es;

import com.usedcar.domain.Listing;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "listings")
public class EsListingDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Keyword)
    private String make;
    @Field(type = FieldType.Keyword)
    private String model;
    @Field(type = FieldType.Integer)
    private Integer year;
    @Field(type = FieldType.Double)
    private Double price;
    @Field(type = FieldType.Integer)
    private Integer mileage;
    @Field(type = FieldType.Keyword)
    private String fuelType;
    @Field(type = FieldType.Keyword)
    private String transmission;
    @Field(type = FieldType.Keyword)
    private String city;
    @Field(type = FieldType.Keyword)
    private String status;
    @Field(type = FieldType.Keyword, index = false)
    private String thumbnailUrl;
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime publishedAt;
    @Field(type = FieldType.Integer)
    private Integer viewCount7d;

    public static EsListingDocument from(Listing l) {
        EsListingDocument d = new EsListingDocument();
        d.id = l.getId();
        d.title = l.getTitle();
        d.make = l.getMake();
        d.model = l.getModel();
        d.year = l.getYear();
        d.price = l.getPrice() == null ? null : l.getPrice().doubleValue();
        d.mileage = l.getMileage();
        d.fuelType = l.getFuelType();
        d.transmission = l.getTransmission();
        d.city = l.getCity();
        d.status = l.getStatus();
        d.thumbnailUrl = l.getPhotoUrls() != null && !l.getPhotoUrls().isEmpty()
                ? l.getPhotoUrls().get(0) : null;
        d.publishedAt = l.getPublishedAt();
        d.viewCount7d = 0;
        return d;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public Integer getViewCount7d() { return viewCount7d; }
    public void setViewCount7d(Integer viewCount7d) { this.viewCount7d = viewCount7d; }
}
