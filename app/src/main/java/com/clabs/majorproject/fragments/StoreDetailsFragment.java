package com.clabs.majorproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.clabs.majorproject.R;
import com.clabs.majorproject.models.FollowerModel;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.singleton.ShopRegistrationSingleton;
import com.clabs.majorproject.util.Constants;

import java.util.ArrayList;

public class StoreDetailsFragment extends Fragment {
//TODO - field validation.
    EditText storeAddressEditText;
    EditText storeDescEditText;
    EditText cityEditText;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_details,container,false);
        storeAddressEditText = (EditText)view.findViewById(R.id.store_address_edit_text);
        storeDescEditText = (EditText)view.findViewById(R.id.store_desc_edit_text);
        cityEditText = (EditText)view.findViewById(R.id.city_edit_text);
        return view;
    }

    public void setStoreDetails(){
        StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();
        storeModel.setDescription(storeDescEditText.getText().toString());
        storeModel.setAddress(storeAddressEditText.getText().toString());
        storeModel.setCity(cityEditText.getText().toString());
        storeModel.setPersonRated(0);
        storeModel.setRating(0);
        storeModel.setStoreType(Constants.RESTAURANT);
        ShopRegistrationSingleton.getInstance().setShopModel(storeModel);
    }
}
