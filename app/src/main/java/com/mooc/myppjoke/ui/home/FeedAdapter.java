package com.mooc.myppjoke.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.myppjoke.BR;
import com.mooc.myppjoke.R;
import com.mooc.myppjoke.databinding.LayoutFeedTypeImageBinding;
import com.mooc.myppjoke.databinding.LayoutFeedTypeVideoBinding;
import com.mooc.myppjoke.model.Feed;
import com.mooc.myppjoke.view.ListPlayerView;

public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    protected Context mContext;
    protected String mCategory;

    protected FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.equals(newItem);
            }
        });

        inflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Feed feed=getItem(position);
        holder.bindData(feed);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Feed
//            }
//        });

    }

    @Override
    public int getItemViewType(int position) {
        Feed feed=getItem(position);
        if (feed.itemType==Feed.TYPE_IMAGE_TEXT){
            return R.layout.layout_feed_type_image;
        }else if (feed.itemType==Feed.TYPE_VIDEO){
            return R.layout.layout_feed_type_video;
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewDataBinding mBinding;
        public ListPlayerView listPlayerView;
        public ImageView feedImage;


        public ViewHolder(@NonNull View itemView, ViewDataBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Feed item) {
            //????????????????????????????????????????????? ?????? ????????????????????????????????????
            //???dataBinding????????????????????????????????????
            //?????????????????????????????? ???????????????????????????????????????????????????

            mBinding.setVariable(BR.feed, item);
//            mBinding.setVariable()
            if (mBinding instanceof LayoutFeedTypeImageBinding) {
                LayoutFeedTypeImageBinding imageBinding = (LayoutFeedTypeImageBinding) mBinding;
                feedImage = imageBinding.feedImage;
                imageBinding.feedImage.bindData(item.width, item.height, 16, item.cover);
            } else if (mBinding instanceof LayoutFeedTypeVideoBinding) {
                LayoutFeedTypeVideoBinding videoBinding = (LayoutFeedTypeVideoBinding) mBinding;
                videoBinding.listPlayerView.bindData(mCategory, item.width, item.height, item.cover, item.url);
                listPlayerView = videoBinding.listPlayerView;
            }
        }
    }
}
