package com.lingfei.android.uilib.base;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lingfei.android.uilib.R;
import com.lingfei.android.uilib.base.inteface.BackHandledInterface;
import com.lingfei.android.uilib.base.inteface.HasComponent;
import com.lingfei.android.uilib.util.ResourceUtils;
import com.lingfei.android.uilib.widget.ProgressBarCircularIndeterminate;

/**
	* BaseFragment通常的Fragment可以继承其
	*/
public abstract class BaseFragment extends ProgressFragment{

				private TextView tvError, tvEmpty, tvLoading;
				private Button btnReload;

				public abstract void initInjector();

				protected BackHandledInterface mBackHandledInterface;

				/**
					* 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
					*/
				public abstract boolean onBackPressed();

				public abstract int initContentView();

				/**
					* 得到Activity传进来的值
					*/
				public abstract void getBundle(Bundle bundle);

				/**
					* 初始化控件
					*/
				public abstract void initUI(View view);

				/**
					* 在监听器之前把数据准备好
					*/
				public abstract void initData();

				@Override
				public void onCreate(Bundle savedInstanceState){
								super.onCreate(savedInstanceState);
								if(!(getActivity() instanceof BackHandledInterface)){
												// 宿主Activity必须实现接口 BackHandledInterface
												throw new ClassCastException(
																				"Hosting Activity must implement BackHandledInterface");
								}
								else{
												this.mBackHandledInterface = (BackHandledInterface) getActivity();
								}
				}

				@Override
				public void onStart(){
								super.onStart();
								// 告诉FragmentActivity，当前Fragment在栈顶
								mBackHandledInterface.setSelectedFragment(this);
				}

				@Override
				public void onViewCreated(View view, Bundle savedInstanceState){
								initInjector();
								getBundle(getArguments());
								initUI(view);
								initData();
								super.onViewCreated(view, savedInstanceState);
				}

				@Override
				public View onCreateContentView(LayoutInflater inflater){
								return inflater.inflate(initContentView(), null);
				}

				@Override
				public View onCreateContentErrorView(LayoutInflater inflater){
								View error = inflater.inflate(R.layout.base_frag_error_view_layout, null);
								tvError = (TextView) error.findViewById(R.id.tvError);
								error.findViewById(R.id.btnReload).setOnClickListener(new View.OnClickListener(){
												@Override
												public void onClick(View view){
																onReloadClicked();
												}
								});
								return error;
				}

				@Override
				public View onCreateContentEmptyView(LayoutInflater inflater){
								View empty = inflater.inflate(R.layout.base_frag_empty_view_layout, null);
								tvEmpty = (TextView) empty.findViewById(R.id.tvEmpty);
								btnReload = (Button) empty.findViewById(R.id.btnReload);
								empty.findViewById(R.id.btnReload).setOnClickListener(new View.OnClickListener(){
												@Override
												public void onClick(View view){
																onReloadClicked();
												}
								});
								return empty;
				}

				@Override
				public View onCreateProgressView(LayoutInflater inflater){
								View loading = inflater.inflate(R.layout.base_frag_loading_view_layout, null);
								tvLoading = (TextView) loading.findViewById(R.id.tvLoading);
								ProgressBarCircularIndeterminate progressBar =
																(ProgressBarCircularIndeterminate) loading.findViewById(R.id.progress_view);
								progressBar.setBackgroundColor(ResourceUtils.getThemeColor(getActivity()));
								return loading;
				}

				public void setErrorText(String text){
								tvError.setText(text);
				}

				public void setErrorText(int textResId){
								setErrorText(getString(textResId));
				}

				public void setEmptyText(String text){
								tvEmpty.setText(text);
				}

				public void setEmptyButtonVisible(int visible){
								btnReload.setVisibility(visible);
				}

				public void setEmptyText(int textResId){
								setEmptyText(getString(textResId));
				}

				public void setLoadingText(String text){
								tvLoading.setText(text);
				}

				public void setLoadingText(int textResId){
								setLoadingText(getString(textResId));
				}

				//Override this to reload
				public void onReloadClicked(){

				}

				public boolean onKeyDown(int keyCode, KeyEvent event){
								return false;
				}

				@SuppressWarnings("unchecked")
				protected <C> C getComponent(Class<C> componentType){
								return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
				}
}
