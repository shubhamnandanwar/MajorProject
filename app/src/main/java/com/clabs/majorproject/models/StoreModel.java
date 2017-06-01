package com.clabs.majorproject.models;

import java.util.ArrayList;
import java.util.List;

public class StoreModel {
    private String id;
    private String name;
    private String address;
    private String imageUri;
    private String description;
    private LatLng latLng;
    private String storeType;
    private float rating;
    private int personRated;
    private List<ReviewModel> reviewModelList;
    private List<FollowerModel> followerModelList;
    private String city;
    private OfferModel offerModel;
    private List<String> verifiedBy = new ArrayList<>();

    public List<String> getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(List<String> verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public OfferModel getOfferModel() {
        return offerModel;
    }

    public void setOfferModel(OfferModel offerModel) {
        this.offerModel = offerModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getPersonRated() {
        return personRated;
    }

    public void setPersonRated(int personRated) {
        this.personRated = personRated;
    }

    public List<ReviewModel> getReviewModelList() {
        return reviewModelList;
    }

    public void setReviewModelList(List<ReviewModel> reviewModelList) {
        this.reviewModelList = reviewModelList;
    }

    public List<FollowerModel> getFollowerModelList() {
        return followerModelList;
    }

    public void setFollowerModelList(List<FollowerModel> followerModelList) {
        this.followerModelList = followerModelList;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
