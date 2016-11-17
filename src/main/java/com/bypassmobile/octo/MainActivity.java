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
import com.bypassmobile.octo.model.User;
import com.google.gson.Gson;

import java.util.List;
import java.util.Observable;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements android.support.v7.widget.SearchView.OnQueryTextListener {


    @Bind(R.id.my_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.github_contact_recycler)
    RecyclerView mRecycler;

    User currentUser;

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

        }else{
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
                updateRecycler(users);
            }
        });

    }

    private void updateRecycler(List<User> users){
        mRecycler.setAdapter(new RecyclerAdapter(users, this, currentUser));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem =  menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
        }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v("avtar", "got new text: "+newText);
        return false;
    }
}
