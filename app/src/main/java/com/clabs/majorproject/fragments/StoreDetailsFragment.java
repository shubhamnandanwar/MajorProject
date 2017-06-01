package com.clabs.majorproject.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.clabs.majorproject.R;
import com.clabs.majorproject.models.FollowerModel;
import com.clabs.majorproject.models.StoreModel;
import com.clabs.majorproject.singleton.ShopRegistrationSingleton;
import com.clabs.majorproject.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class StoreDetailsFragment extends Fragment {
//TODO - field validation.
    EditText storeAddressEditText;
    EditText storeDescEditText;
    EditText cityEditText;
    Spinner spinner;
    String storeType;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_details,container,false);
        storeAddressEditText = (EditText)view.findViewById(R.id.store_address_edit_text);
        storeDescEditText = (EditText)view.findViewById(R.id.store_desc_edit_text);
        cityEditText = (EditText)view.findViewById(R.id.city_edit_text);
        spinner = (Spinner)view.findViewById(R.id.spinner);

        final List<String> typeList = new ArrayList<>();
        typeList.add(Constants.RESTAURANT);
        typeList.add(Constants.COFFEE_SHOP);
        typeList.add(Constants.STORE);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), R.layout.sender_name_spinner_item, R.id.class_name, typeList);
        dataAdapter.setDropDownViewResource(R.layout.sender_name_spinner_item);
        spinner.setAdapter(dataAdapter);

        storeType = typeList.get(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                storeType = typeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    public void setStoreDetails(){
        StoreModel storeModel = ShopRegistrationSingleton.getInstance().getShopModel();
        storeModel.setDescription(storeDescEditText.getText().toString());
        storeModel.setAddress(storeAddressEditText.getText().toString());
        storeModel.setCity(cityEditText.getText().toString());
        storeModel.setPersonRated(0);
        storeModel.setRating(0);
        storeModel.setStoreType(storeType);
        ShopRegistrationSingleton.getInstance().setShopModel(storeModel);
    }
}
