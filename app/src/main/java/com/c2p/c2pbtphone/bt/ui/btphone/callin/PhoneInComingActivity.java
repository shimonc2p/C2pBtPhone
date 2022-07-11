package com.c2p.c2pbtphone.bt.ui.btphone.callin;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.c2p.c2pbtphone.R;
import com.c2p.c2pbtphone.bt.ui.BaseSupportActivity;
import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTPhonePeople;
import com.c2p.c2pbtphone.bt.ui.btphone.event.BTContactEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.event.BTStatuEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.util.BTControler;
import com.lingfei.android.uilib.util.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class PhoneInComingActivity extends BaseSupportActivity implements View.OnFocusChangeListener{

				@BindView(R.id.ib_menu_button)
				ImageButton ib_menu_button;

				@BindView(R.id.ib_menu_button_left)
				ImageButton ib_menu_button_left;

				@BindView(R.id.ib_menu_button_right)
				ImageButton ib_menu_button_right;

				@BindView(R.id.iv_item_selected)
				ImageView iv_item_selected;

				@BindView(R.id.tv_people_name)
				TextView tv_people_name;

				@BindView(R.id.btn_to_answer)
				Button btn_to_answer;

				@BindView(R.id.btn_to_hangup)
				Button btn_to_hangup;

				@OnClick(R.id.ib_menu_button_right)
				public void controlButtonRightOnClick(){

				}

				@OnClick(R.id.ib_menu_button_left)
				public void controlButtonLeftOnClick(){
								finish();
				}

				@OnClick({R.id.btn_to_answer, R.id.btn_to_hangup})
				public void onclick(View v){
								v.requestFocusFromTouch();
								switch(v.getId()){
												case R.id.btn_to_answer:{
																BTControler.getInstance().toAnswer();
																break;
												}
												case R.id.btn_to_hangup:{
																BTControler.getInstance().toHangup();
																break;
												}
												default:
																break;
								}
								finish();
				}

				public void initPhoneInComingView(){
								Intent intent = getIntent();
								String phoneName = intent.getStringExtra("comingPhoneName");
								String phoneNumber = intent.getStringExtra("comingPhoneNumber");
								onShowPhoneNumber(phoneName, phoneNumber);

								btn_to_answer.setOnFocusChangeListener(this);
								btn_to_hangup.setOnFocusChangeListener(this);
				}

				private void onShowPhoneNumber(String name, String number){
								if(StringUtil.isNotEmpty(name)){
												number = name; // 姓名不为空，显示姓名
								}
								tv_people_name.setText(" " +number + " ");
				}

				@Subscribe(threadMode = ThreadMode.MainThread)
				public void onEventBTContactChange(BTContactEvent event){
								if(null != event){
												switch(event.getStatu()){
																case CALL_IN:{
																				BTPhonePeople phonePeople = event.getPhonePeople();
																				if(null != phonePeople){
																								String name = phonePeople.getPeopleName();
																								String number = phonePeople.getPhoneNumber();
																								onShowPhoneNumber(name, number);
																				}
																				break;
																}
												}
								}
				}

				@Subscribe(threadMode = ThreadMode.MainThread)
				public void onEventBTStatuChange(BTStatuEvent event){
								if(null != event){
												switch(event.getStatu()){
																case DISCONNECTED:
																case OFF_LINE:
																case HANG_UP:
																case TALKING:{
																				finish();
																				break;
																}
												}
								}
				}

				@Override
				public void onFocusChange(View v, boolean hasFocus){
								if(hasFocus){
												switch(v.getId()){
																case R.id.btn_to_answer:
																				iv_item_selected.setImageResource(R.drawable.ic_bt_callin_item_selected_0);
																				break;
																case R.id.btn_to_hangup:
																				iv_item_selected.setImageResource(R.drawable.ic_bt_callin_item_selected_1);
																				break;
																default:
																				break;
												}
								}
				}

				@Override
				public boolean dispatchKeyEvent(KeyEvent event){
								switch(event.getKeyCode()){
												case KeyEvent.KEYCODE_DPAD_LEFT:
																if(event.getAction() == KeyEvent.ACTION_DOWN){
																				ib_menu_button_left.setImageResource(R.drawable.ic_control_menu_left_focus);
																}
																else if(event.getAction() == KeyEvent.ACTION_UP || event.getAction() == KeyEvent.FLAG_CANCELED){
																				ib_menu_button_left.setImageResource(R.drawable.ic_control_menu_left_normal);
																				controlButtonLeftOnClick();
																}
																return true;
												case KeyEvent.KEYCODE_DPAD_RIGHT:
																if(event.getAction() == KeyEvent.ACTION_DOWN){
																				ib_menu_button_right.setImageResource(R.drawable.ic_control_menu_right_focus);
																}
																else if(event.getAction() == KeyEvent.ACTION_UP){
																				ib_menu_button_right.setImageResource(R.drawable.ic_control_menu_right_normal);
																				controlButtonRightOnClick();
																}
																return true;
												case KeyEvent.KEYCODE_ENTER:
												case KeyEvent.KEYCODE_DPAD_CENTER:{
																if(event.getAction() == KeyEvent.ACTION_DOWN){
																				ib_menu_button.setImageResource(R.drawable.ic_control_menu_fucos);
																}
																else if(event.getAction() == KeyEvent.ACTION_UP || event.getAction() == KeyEvent.FLAG_CANCELED){
																				ib_menu_button.setImageResource(R.drawable.ic_control_menu_normal);
																}
																break;
												}
												default:
																break;
								}
								return super.dispatchKeyEvent(event);
				}

				@Override
				public int initContentView(){
								return R.layout.phone_in_coming_activity;
				}

				@Override
				public void initInjector(){

				}

				@Override
				public void initUiAndListener(Bundle savedInstanceState){
								ll_toolbar.setVisibility(View.GONE);
								ib_home.setVisibility(View.GONE);
								ButterKnife.bind(this);
								EventBus.getDefault().register(this);
								initPhoneInComingView();
								setFinishOnTouchOutside(false);
				}

				@Override
				public void onWindowFocusChanged(boolean hasFocus){
								super.onWindowFocusChanged(hasFocus);
								if(hasFocus){
												btn_to_answer.requestFocusFromTouch();
												btn_to_answer.requestFocus();
								}
				}

				@Override
				protected void onDestroy(){
								super.onDestroy();
								EventBus.getDefault().unregister(this);
								
				}
}
