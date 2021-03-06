package com.mooc.myppjoke.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.alibaba.fastjson.TypeReference;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.mooc.libnetwork.Request;
import com.mooc.myppjoke.model.Feed;
import com.mooc.myppjoke.ui.AbsViewModel;
import com.mooc.myppjoke.ui.MutablePageKeyedDataSource;
import com.mooc.myppjoke.ui.login.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean witchCache=true;

    private MutableLiveData<PagedList<Feed>> cacheLiveData=new MutableLiveData<>();
    private AtomicBoolean loadAfter=new AtomicBoolean();
    private String mFeedType;


    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData(){
        return cacheLiveData;
    }

    public void setFeedType(String feedType){
        mFeedType=feedType;
    }


    class FeedDataSource extends ItemKeyedDataSource<Integer,Feed>{

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            //加载初始化数据的
            Log.e("homeviewmodel","loadInitial");
            loadData(0,params.requestedLoadSize,callback);
            witchCache=false;
        }

        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向后加载分页数据
            Log.e("homeviewmodel","loadAfter");
            loadData(params.key,params.requestedLoadSize,callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            //向前加载数据
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return null;
        }
    }

    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key>0){
            loadAfter.set(true);
        }

        //feeds/queryHotFeedsList
        Request request= ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType",mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId",key)
                .addParam("pageCount",count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());

        if (witchCache){
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    Log.e("loadData","onCacheSuccess");
                    MutablePageKeyedDataSource dataSource=new MutablePageKeyedDataSource();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList=dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);
                }
            });
        }


        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;

            callback.onResult(data);

            if (key > 0) {
                //通过BoundaryPageData发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
                loadAfter.set(false);
            }


        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Log.e("loadData","loadData:key:"+key);

    }


    @SuppressLint("RestrictedApi")
    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback){
        if (loadAfter.get()){
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id,config.pageSize,callback);
            }
        });
    }

}