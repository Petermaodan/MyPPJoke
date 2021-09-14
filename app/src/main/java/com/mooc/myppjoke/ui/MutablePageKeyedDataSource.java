package com.mooc.myppjoke.ui;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutablePageKeyedDataSource<Value> extends PageKeyedDataSource<Integer,Value> {
    public List<Value> data=new ArrayList<>();

    //构建一个pagedList实例，在viewModel中通过MutableLiveData将请求的pagedList通过postValue传递到可变的MutableLiveData上面，在ui线程，通过liveData的观察者模式去
    //判断数据是否发生改变，从而判断是否需要更新数据

    public PagedList<Value> buildNewPagedList(PagedList.Config config){
        @SuppressLint("RestrictedApi") PagedList<Value> pagedList=new PagedList.Builder<Integer,Value>(this,config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();

        return pagedList;
    }


    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Value> callback) {
        callback.onResult(data,null,null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Value> callback) {
        callback.onResult(Collections.emptyList(),null);
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Value> callback) {
        callback.onResult(Collections.emptyList(),null);
    }
}
