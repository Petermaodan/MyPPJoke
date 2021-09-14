package com.mooc.myppjoke.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.libcommon.view.EmptyView;
import com.mooc.myppjoke.R;
import com.mooc.myppjoke.databinding.LayoutRefreshViewBinding;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbsListFragment<T,M extends AbsViewModel<T>> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    protected LayoutRefreshViewBinding binding;
    protected RecyclerView mRecyclerView;
    protected SmartRefreshLayout mRefreshLayout;
    protected EmptyView mEmptyView;
    protected PagedListAdapter<T, RecyclerView.ViewHolder> adapter;
//    protected M mViewModel;
    protected DividerItemDecoration decoration;
    protected M mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=LayoutRefreshViewBinding.inflate(inflater,container,false);
        binding.getRoot().setFitsSystemWindows(true);
        mRecyclerView=binding.recyclerView;
        mRefreshLayout=binding.refreshLayout;
        mEmptyView=binding.emptyView;

        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        adapter=getAdapter();
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setItemAnimator(null);

        //默认给列表中的Item 一个 10dp的ItemDecoration
        decoration=new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);



        genericViewModel();
        return binding.getRoot();

    }

    private void genericViewModel() {
        //利用 子类传递的 泛型参数实例化出absViewModel 对象。
        //getClass().getGenericSuperclass()返回表示此 Class 所表示的实体
        ParameterizedType type= (ParameterizedType) getClass().getGenericSuperclass();
        // getActualTypeArguments()返回表示此类型实际类型参数的 Type 对象的数组。
        //总的来说就是通过反射获取子类中的泛型对象，arguments[1]表示是第二个泛型参数，也就输ViewModel
        Type[] arguments=type.getActualTypeArguments();
        if (arguments.length>1){
            Type argument=arguments[1];
            Class modelClaz=((Class)argument).asSubclass(AbsViewModel.class);
            mViewModel= (M) ViewModelProviders.of(this).get(modelClaz);

            //触发页面初始化数据加载的逻辑
            mViewModel.getPageData().observe(getViewLifecycleOwner(), pagedList -> submitList(pagedList));

            //监听分页时有无更多数据,以决定是否关闭上拉加载的动画
            mViewModel.getBoundaryPageData().observe(getViewLifecycleOwner(), hasData -> finishRefresh(hasData));

        }
    }

    public void submitList(PagedList<T> result){
        //只有当新数据集合大于0 的时候，才调用adapter.submitList
        //否则可能会出现 页面----有数据----->被清空-----空布局
        if (result.size()>0){
            adapter.submitList(result);
        }
        finishRefresh(result.size()>0);
    }


    //数据请求和刷新
    public void finishRefresh(boolean hasData){
        PagedList<T> currentList=adapter.getCurrentList();
        hasData=hasData||currentList!=null&&currentList.size()>0;
        //获得刷新叶的状态
        RefreshState state=mRefreshLayout.getState();

        //实现下拉刷新
        if (state.isFooter&&state.isOpening){
            mRefreshLayout.finishLoadMore();
        }else if (state.isHeader&&state.isOpening){
            mRefreshLayout.finishRefresh();
        }

        if (hasData){
            mEmptyView.setVisibility(View.GONE);
        }else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }




    /**
     * 因而 我们在 onCreateView的时候 创建了 PagedListAdapter
     * 所以，如果arguments 有参数需要传递到Adapter 中，那么需要在getAdapter()方法中取出参数。
     *
     * @return
     */
    public abstract PagedListAdapter getAdapter();
}
