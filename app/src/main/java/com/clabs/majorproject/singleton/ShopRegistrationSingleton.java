package com.clabs.majorproject.singleton;

import com.clabs.majorproject.models.StoreModel;

/**
 * Created by shubh on 28-02-2017.
 */
public class ShopRegistrationSingleton {

    private static ShopRegistrationSingleton shopRegistrationSingleton;
    private static StoreModel storeModel;

    private ShopRegistrationSingleton(){}

    public static ShopRegistrationSingleton getInstance() {
        if (shopRegistrationSingleton == null)
            return new ShopRegistrationSingleton();
        return shopRegistrationSingleton;
    }

    public StoreModel getShopModel() {
        if (storeModel == null)
            return new StoreModel();
        return storeModel;
    }

    public void setShopModel(StoreModel storeModel) {
        this.storeModel = storeModel;
    }
}
