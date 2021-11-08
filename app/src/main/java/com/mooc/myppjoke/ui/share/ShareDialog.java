package com.mooc.myppjoke.ui.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.libcommon.utils.PixUtils;
import com.mooc.libcommon.view.CornerFrameLayout;
import com.mooc.libcommon.view.ViewHelper;
import com.mooc.myppjoke.R;
import com.mooc.myppjoke.view.PPImageView;

import java.util.ArrayList;
import java.util.List;

public class ShareDialog extends AlertDialog {

    private ShareAdapter shareAdapter;
    private String shareContent;

    private CornerFrameLayout layout;
    private View.OnClickListener mListener;

    public ShareDialog(@NonNull Context context) {
        super(context);
    }

    protected ShareDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShareDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    List<ResolveInfo> shareitems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //获取CornerFrameLayout的实例以及设置背景
        layout=new CornerFrameLayout(getContext());
        layout.setBackgroundColor(Color.WHITE);
        layout.setViewOutline(PixUtils.dp2px(20), ViewHelper.RADIUS_TOP);

        //在网格中布局项目的LayoutManager实现。
        RecyclerView gridView=new RecyclerView(getContext());
        gridView.setLayoutManager(new GridLayoutManager(getContext(),4));

        shareAdapter=new ShareAdapter();
    }

    public void setShareContent(String shareContent){
        this.shareContent=shareContent;
    }

    //这里用回调的方式调用分享面板以及点击要分享的按钮
    public void setShareItemClickListener(View.OnClickListener listener){
        mListener=listener;
    }

    //添加查询路口querySharaItems()方法
    private void queryShareItems(){
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");

        List<ResolveInfo> resolveInfos=getContext().getPackageManager().queryIntentActivities(intent,0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName=resolveInfo.activityInfo.packageName;
            if (TextUtils.equals(packageName,"com.tencent.mm")||TextUtils.equals(packageName, "com.tencent.mobileqq")){
                shareitems.add(resolveInfo);
            }
        }
        //调用notifyDataSetChanged()方法进行刷新即可
        shareAdapter.notifyDataSetChanged();

    }

    private class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //用于检索与当前安装在设备上的应用程序包相关的各种信息
        private final PackageManager packageManager;

        private ShareAdapter() {
            packageManager=getContext().getPackageManager();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //载入布局
            View inflate= LayoutInflater.from(getContext()).inflate(R.layout.layout_share_item,parent,false);
            return new RecyclerView.ViewHolder(inflate) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //载入分享的图标，通过PackageManager获取到安装应用的包名
            ResolveInfo resolveInfo=shareitems.get(position);
            PPImageView imageView=holder.itemView.findViewById(R.id.share_icon);
            Drawable drawable=resolveInfo.loadIcon(packageManager);
            imageView.setImageDrawable(drawable);

            //载入文字
            TextView shareText=holder.itemView.findViewById(R.id.share_text);
            shareText.setText(resolveInfo.loadLabel(packageManager));

            //创建图标的点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    String pkg=resolveInfo.activityInfo.packageName;
                    String cls=resolveInfo.activityInfo.name;
                    Intent intent=new Intent();
                    intent.setType("text/plain");
                    intent.setComponent(new ComponentName(pkg,cls));
                    intent.putExtra(Intent.EXTRA_TEXT,shareContent);

                    //启动Activity
                    getContext().startActivity(intent);

                    if (mListener!=null){
                        mListener.onClick(v);
                    }
                    dismiss();
                }
            });

        }

        @Override
        public int getItemCount() {
            return shareitems==null?0:shareitems.size();
        }
    }
}
