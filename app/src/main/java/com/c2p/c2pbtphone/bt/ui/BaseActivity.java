package com.c2p.c2pbtphone.bt.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.c2p.c2pbtphone.R;
import com.c2p.c2pbtphone.bt.BaseApplication;
import com.c2p.c2pbtphone.bt.injector.Module.ActivityModule;
import com.c2p.c2pbtphone.bt.injector.component.ApplicationComponent;
import com.lingfei.android.uilib.AppManager;


/**
	* The parent class of Activity, inheriting this class can monitor the information sent by MCU
	*/
public abstract class BaseActivity extends AppCompatActivity {

				@Override
				protected void onCreate(Bundle savedInstanceState){
								getApplicationComponent().inject(this);
								super.onCreate(savedInstanceState);
	       /* set it to be full screen */
								getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								                     WindowManager.LayoutParams.FLAG_FULLSCREEN);

								//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 设置activity为竖屏显示
								setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置activity为横屏显示
								setContentView(initContentView());
								initInjector();
								initUiAndListener();
								AppManager.getAppManager().addActivity(this);
				}

				protected ApplicationComponent getApplicationComponent(){
								return ((BaseApplication) getApplication()).getApplicationComponent();
				}

				protected ActivityModule getActivityModule(){
								return new ActivityModule(this);
				}

				public void initToolBar(Toolbar mToolBar){
								if(null != mToolBar){
												setSupportActionBar(mToolBar);
												getSupportActionBar().setDisplayShowHomeEnabled(false);
												getSupportActionBar().setDisplayHomeAsUpEnabled(true);
								}
				}

				public void openActivity(Class<?> pClass){
								openActivity(pClass, null);
				}

				public void openActivity(Class<?> pClass, Bundle pBundle){
								Intent intent = new Intent(this, pClass);
								if(pBundle != null){
												intent.putExtras(pBundle);
								}
								startActivity(intent);
								//        overridePendingTransition(R.anim.translate_left_in, R.anim.translate_left_out);
				}

				public void openActivityForResult(Class<?> pClass, Bundle pBundle, int requestCode){
								Intent intent = new Intent(this, pClass);
								if(pBundle != null){
												intent.putExtras(pBundle);
								}
								startActivityForResult(intent, requestCode);
								overridePendingTransition(R.anim.translate_left_in, R.anim.translate_left_out);
				}

				protected void openActivity(String pAction){
								openActivity(pAction, null);
				}

				protected void openActivity(String pAction, Bundle pBundle){
								Intent intent = new Intent(pAction);
								if(pBundle != null){
												intent.putExtras(pBundle);
								}
								startActivity(intent);
								overridePendingTransition(R.anim.translate_left_in, R.anim.translate_left_out);
				}

				protected void openActivityForResult(String pAction, Bundle pBundle, int requestCode){
								Intent intent = new Intent(pAction);
								if(pBundle != null){
												intent.putExtras(pBundle);
								}
								startActivityForResult(intent, requestCode);
								overridePendingTransition(R.anim.translate_left_in, R.anim.translate_left_out);
				}

				public void hideKeyboard(View view){
								InputMethodManager imm = (InputMethodManager) this
																.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(view.getWindowToken(),
								                            InputMethodManager.HIDE_NOT_ALWAYS);
				}

				public void defaultFinish(){
								super.finish();
								//        overridePendingTransition(0, 0);
				}

				@Override
				public boolean onKeyDown(int keyCode, KeyEvent event){
								if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
												defaultFinish();
								}
								return super.onKeyDown(keyCode, event);
				}

				/**
					* @param @param  layoutResID
					* @param @param  root
					* @param @return 设定文件
					* @return View    返回类型
					* @throws
					* @Title: inflate
					* @Description: 加载布局的view
					*/
				protected View inflate(int layoutResID, ViewGroup root){
								LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View view = layoutInflater.inflate(layoutResID, root, false);
								return view;
				}

				/**
					* 设置view
					*/
				public abstract int initContentView();

				/**
					* 注入Injector
					*/
				public abstract void initInjector();

				/**
					* init UI && Listener
					*/
				public abstract void initUiAndListener();

				public void reload(){
								Intent intent = getIntent();
								overridePendingTransition(0, 0);
								intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
								finish();
								overridePendingTransition(0, 0);
								startActivity(intent);
				}

				@Override
				public boolean onOptionsItemSelected(MenuItem item){
								if(item.getItemId() == android.R.id.home){
												finish();
												return true;
								}
								return super.onOptionsItemSelected(item);
				}

				@Override
				protected void onDestroy(){
								super.onDestroy();
								AppManager.getAppManager().finishActivity(this);
				}
}
