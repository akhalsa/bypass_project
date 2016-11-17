package com.bypassmobile.octo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bypassmobile.octo.adapters.RecyclerAdapter;
import com.bypassmobile.octo.model.SearchResponse;
import com.bypassmobile.octo.model.User;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.jakewharton.rxbinding.support.v7.widget.SearchViewQueryTextEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity{
    @Bind(R.id.my_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.github_contact_recycler)
    RecyclerView mRecycler;

    User currentUser;
    List<User> noSearchList;
    Boolean isSearching = false;

    private static final String USER_EXTRA_LABEL = "user_extra_label";

    public static Intent getUserIntent(User u, Context context){
        Intent i = new Intent(context, MainActivity.class);
        Gson g = new Gson();
        i.putExtra(USER_EXTRA_LABEL, g.toJson(u));
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        rx.Observable<List<User>> userObservable;
        currentUser = null;
        if(getIntent().getStringExtra(MainActivity.USER_EXTRA_LABEL) != null){
            Gson g = new Gson();
            currentUser = g.fromJson(getIntent().getStringExtra(USER_EXTRA_LABEL), User.class);
            userObservable = getEndpoint().getFollowingUser(currentUser.getName());

        } else{
            userObservable = getEndpoint().getOrganizationMember("bypasslane");
        }
        if(currentUser != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        userObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<List<User>>() {
            @Override
            public void call(List<User> users) {
                //we need to store the noSearchList as a default
                noSearchList = users;
                updateRecycler(users);
            }
        });
    }


    private void monitorSearchView(SearchView searchView){
        RxSearchView.queryTextChangeEvents(searchView)
                .debounce(750, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .flatMap(new Func1<SearchViewQueryTextEvent, rx.Observable<SearchResponse>>() {
                    @Override
                    public rx.Observable<SearchResponse> call(SearchViewQueryTextEvent searchViewQueryTextEvent) {
                        if(searchViewQueryTextEvent.queryText().length() == 0){
                            //if we have no query, we want to reset the value to the no search list we found initially
                            //we will want isSearching to be false as well so that if we are several pages in
                            //we will still see "$Username follows" in the first cell
                            isSearching = false;
                            SearchResponse searchResponse = new SearchResponse();
                            searchResponse.setItems(noSearchList);
                            return rx.Observable.just(searchResponse);
                        }else{
                            //ok we have a valid search, lets go ahead and get the possible matches out of the api
                            isSearching = true;
                            return getEndpoint().searchUsers(searchViewQueryTextEvent.queryText().toString())
                                    .onErrorReturn(new Func1<Throwable, SearchResponse>() {
                                        @Override
                                        public SearchResponse call(Throwable throwable) {
                                            return new SearchResponse();
                                        }
                                    });
                        }

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<SearchResponse, List<User>>() {
                    @Override
                    public List<User> call(SearchResponse searchResponse) {
                        return searchResponse.getItems();
                    }
                })
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        updateRecycler(users);
                    }
                });
    }
    private void updateRecycler(List<User> users){
        //if were searching we always want to pass null as the current user
        //this is because we wouldnt want the adapter to show the header cell
        mRecycler.setAdapter(new RecyclerAdapter(users, this, isSearching ? null : currentUser));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem =  menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        monitorSearchView(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
