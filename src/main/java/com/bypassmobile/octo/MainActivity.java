package com.bypassmobile.octo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bypassmobile.octo.adapters.RecyclerAdapter;
import com.bypassmobile.octo.model.User;
import com.google.gson.Gson;

import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {


    @Bind(R.id.my_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.github_contact_recycler)
    RecyclerView mRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        getEndpoint().getOrganizationMember("bypasslane")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                /*.flatMap(new Func1<List<User>, rx.Observable<User>>() {
                    @Override
                    public rx.Observable<User> call(List<User> users) {
                        return rx.Observable.from(users);
                    }
                })*/
                .subscribe(new Action1<List<User>>() {
                    @Override
                    public void call(List<User> users) {
                        updateRecycler(users);
                    }
                });

    }

    private void updateRecycler(List<User> users){
        mRecycler.setAdapter(new RecyclerAdapter(users, this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
