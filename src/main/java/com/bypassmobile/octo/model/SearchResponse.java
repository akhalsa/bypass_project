package com.bypassmobile.octo.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by avtarkhalsa on 11/17/16.
 */
public class SearchResponse {

    @SerializedName("items")
    private List<User> items;

    public List<User> getItems() {
        return items;
    }
}
