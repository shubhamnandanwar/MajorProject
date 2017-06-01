package com.clabs.majorproject.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shubh on 15-02-2017.
 */
public class OfferModel {
    private String offerId;
    private String productDesc;
    private int discount;
    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    private List<String> customerList = new ArrayList<>();

    public List<String> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<String> customerList) {
        this.customerList = customerList;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}
