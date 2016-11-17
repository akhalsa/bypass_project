package com.bypassmobile.octo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bypassmobile.octo.MainActivity;
import com.bypassmobile.octo.R;
import com.bypassmobile.octo.image.ImageLoader;
import com.bypassmobile.octo.model.User;
import com.bypassmobile.octo.thirdparty.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by avtarkhalsa on 11/15/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.UserHolder> {

    private List<User> mUsers;
    private Context mContext;
    private Picasso mImageLoader;
    private boolean currentUserFirst;

    private final int DEFAULT_ROW = 0;
    private final int CURRENT_USER_ROW = 1;
    public RecyclerAdapter(List<User> users, Context context, User currentUser) {
        mUsers = new ArrayList<User>(users);
        if( currentUser != null){
            mUsers.add(0, currentUser);
            currentUserFirst = true;
        }
        mContext = context;
        mImageLoader = ImageLoader.createImageLoader(mContext);
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_row, parent, false);

        return new UserHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.bindUser(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return  mUsers!= null ? mUsers.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 ) && currentUserFirst ? CURRENT_USER_ROW : DEFAULT_ROW;
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.avatar_imageview)
        ImageView mAvatarImageView;

        @Bind(R.id.username_textview)
        TextView mUsernameTextView;

        int mType;

        public UserHolder(View itemView, int type) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mType = type;
            if(type == DEFAULT_ROW){
                itemView.setOnClickListener(this);
            }else{
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.height = 48;
                mAvatarImageView.setVisibility(View.INVISIBLE);

            }
        }

        public void bindUser(User user) {
            if(mType == DEFAULT_ROW){
                mImageLoader.load(user.getProfileURL())
                        .transform(new CircleTransform())
                        .into(mAvatarImageView);
                mUsernameTextView.setText(user.getName());
            } else if(mType == CURRENT_USER_ROW){
                mUsernameTextView.setText(user.getName()+" follows");
            }

        }

        @Override
        public void onClick(View v) {
            User u = mUsers.get(getAdapterPosition());
            mContext.startActivity(MainActivity.getUserIntent(u, mContext));
        }
    }
}
