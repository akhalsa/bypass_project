package com.bypassmobile.octo.rest;


import com.bypassmobile.octo.model.SearchResponse;
import com.bypassmobile.octo.model.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface GithubEndpoint {

    public static final String SERVER = "https://api.github.com";

    @GET("/users/{id}")
    public Observable<User> getUser(@Path("id") String user);

    @GET("/users/{id}/following")
    public Observable<List<User>> getFollowingUser(@Path("id") String user);

    @GET("/orgs/{id}/members")
    public Observable<List<User>> getOrganizationMember(@Path("id") String organization);

    @GET("/search/users")
    public Observable<SearchResponse> searchUsers(@Query("q") String query);
}
