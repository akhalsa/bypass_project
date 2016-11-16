package com.bypassmobile.octo.adapters;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bypassmobile.octo.R;
import com.bypassmobile.octo.image.ImageLoader;
import com.bypassmobile.octo.model.User;
import com.bypassmobile.octo.thirdparty.CircleTransform;
import com.squareup.picasso.Picasso;
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

    public RecyclerAdapter(List<User> users, Context context) {
        mUsers = users;
        mContext = context;
        mImageLoader = ImageLoader.createImageLoader(mContext);
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_row, parent, false);
        return new UserHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        holder.bindUser(mUsers.get(position%(mUsers.size())));
    }

    @Override
    public int getItemCount() {
        return mUsers.size()*8;
    }

    public class UserHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.avatar_imageview)
        ImageView mAvatarImageView;

        @Bind(R.id.username_textview)
        TextView mUsernameTextView;

        public UserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bindUser(User user) {
            mImageLoader.load(user.getProfileURL())
                    .transform(new CircleTransform())
                    .into(mAvatarImageView);
            mUsernameTextView.setText(user.getName());
        }
    }
}
