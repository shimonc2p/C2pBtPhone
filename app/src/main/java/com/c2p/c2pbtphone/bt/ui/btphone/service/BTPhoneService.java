package com.c2p.c2pbtphone.bt.ui.btphone.service;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;

import com.c2p.c2pbtphone.bt.ui.btmusic.bean.BTMusic;
import com.c2p.c2pbtphone.bt.ui.btmusic.event.BTMusicEvent;
import com.c2p.c2pbtphone.bt.ui.btmusic.event.FinishBTMusicEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTDevice;
import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTPhonePeople;
import com.c2p.c2pbtphone.bt.ui.btphone.callback.GocsdkCallback;
import com.c2p.c2pbtphone.bt.ui.btphone.event.BTContactEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.event.BTDeviceEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.event.BTSearchContactDoneEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.event.BTStatuEvent;
import com.c2p.c2pbtphone.bt.ui.btphone.util.BTControler;
import com.c2p.c2pbtphone.bt.ui.btphone.util.BTStatu;
import com.lingfei.android.business.cmd.receive.switchview.SwitchUtil;
import com.lingfei.android.business.cmd.send.BTPhoneCmd;
import com.lingfei.android.uilib.util.Constant;
import com.lingfei.android.uilib.util.EventPostUtils;
import com.lingfei.android.uilib.util.LoggerUtil;
import com.lingfei.android.uilib.util.PinYinUtils;
import com.lingfei.android.uilib.util.ShowLogUtil;
import com.lingfei.android.uilib.util.StringUtil;
import com.lingfei.android.uilib.util.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class BTPhoneService extends Service{
				private BTControler.ServiceToken mToken = null;
				private AudioManager audioManager;
				private Map<String, BTPhonePeople> phoneBookMap = new HashMap<>();
				private Map<String, String> contactMap = new HashMap<>(); // Used to save contact updates to peers

				private boolean isPhoneBookDone = false; // Indicates whether the phonebook has been updated

				public void setToast(final String tip){
								if(StringUtil.isNotEmpty(tip)){
												Handler handler = new Handler(Looper.getMainLooper());
												handler.post(new Runnable(){
																@Override
																public void run(){
																				ToastUtils.showToast(tip);
																}
												});
								}
				}

				public void checkCurrentBTDeviceAddr(BTDevice btDevice){
								String currentAddr = BTControler.getInstance().getCurrentAddr();
								if(!currentAddr.equals(btDevice.getDeviceAddr())){
												BTControler.getInstance().setCurrentAddr(btDevice.getDeviceAddr());
												BTControler.getInstance().openPhoneBook(); // Query phone book contacts
								}
								else{
												if(phoneBookMap != null && phoneBookMap.isEmpty()){
																BTControler.getInstance().openPhoneBook(); // Query phone book contacts
												}
								}
				}

				// ??????????????? ?????? ??????
				public void initServcieData(boolean isConnected){
								BTControler.getInstance().setBTHfpConnected(isConnected);
								int btState = 0;
								if(isConnected){ // ?????????
												// ????????????????????????
												BTControler.getInstance().openPhoneBook();
												btState = 1;
								}
								else{
												isPhoneBookDone = false;
												BTControler.getInstance().setCurrentAddr("");
												BTControler.getInstance().clearPhonePeoples();
												if(null != phoneBookMap){
																phoneBookMap.clear();
												}
												if(null != contactMap){
																contactMap.clear();
												}
												notifyUpdatePhoneBook();
								}
				}

				/**
					* ????????????????????????
					*
					* @param state 0???????????????1????????????
					*/
				private void notifyBTStateTaiking(int state){
								Intent intent = new Intent();
								intent.setAction(Constant.Action.ACTION_BT_STATE_TAIKING);
								intent.putExtra("state", state);
								intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
								sendBroadcast(intent); // ????????????
				}

				// ???????????????
				private void notifyUpdatePhoneBook(){
								List<BTPhonePeople> phonePeoples = new ArrayList<>(phoneBookMap.values());
								EventPostUtils.Post(new BTSearchContactDoneEvent(phonePeoples));
				}


				/* Synchronize the bluetooth status to the peer by broadcasting, to solve the problem that the peer's voice and Bluetooth phone cannot coexist
				The corresponding relationship of Bluetooth status value is as follows:
				1000: Bluetooth connection
				1001: Bluetooth disconnected
				1002: Bluetooth is idle and available (connected)
				1003: Incoming call from Bluetooth phone
				1004: Bluetooth call refused
				1005: Bluetooth call answering (calling)
				1006: Bluetooth phone hang up
				1007: Bluetooth Active Dial*/
				private void sendBtPhoneBroadcast(int value, String name, String num){
								// ??????????????????
								SystemProperties.set(Constant.KEY_BT_STATE, String.valueOf(value));

								Intent intent = new Intent(Constant.Action.ACTION_LF_TXZ_BT_STATE);
								intent.putExtra("btphone", value); // value?????????????????????
								if(StringUtil.isNotEmpty(name)){
												intent.putExtra("name", name);
								}
								if(StringUtil.isNotEmpty(num)){
												intent.putExtra("num", num);
								}
								sendBroadcast(intent);
				}

				/**
					* ????????????????????????
					*
					* @param number
					* @return
					*/
				private String getPhoneNameByNumber(String number){
								String name = number;
								if(phoneBookMap != null && !phoneBookMap.isEmpty()){
												ShowLogUtil.show("??????????????????");
												BTPhonePeople btPhonePeople = phoneBookMap.get(number);
												if(null != btPhonePeople){
																name = btPhonePeople.getPeopleName();
																ShowLogUtil.show("????????????");
												}
								}
								else{
												ShowLogUtil.show("???????????????,????????????");
								}
								return name;
				}

				//======================audio focus======================
				public void initAudioManage(){
								audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
								int index = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
								LoggerUtil.e("LingFei  voice call index = " + index);
								// ?????????????????????
								audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, index, 0);
				}

				// ????????????
				public void requestAudioFocus(){
								if(audioManager != null){
												int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
								}
				}

				// ??????????????????
				public void abandonAudioFocus(){
								if(audioManager != null){
												audioManager.abandonAudioFocus(afChangeListener);
								}
				}

				AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener(){
								@Override
								public void onAudioFocusChange(int focusChange){
								}
				};

				/**
					* ?????????????????????????????????
					*
					* @param btPeople
					*/
				public void onSaveBTPhonePeople(BTPhonePeople btPeople){
								if(btPeople != null && StringUtil.isNotEmpty(btPeople.getPhoneNumber())){
												// save contacts
												phoneBookMap.put(btPeople.getPhoneNumber(), btPeople);
												contactMap.put(btPeople.getPhoneNumber(), btPeople.getPeopleName());
								}
				}

				/**
					* ????????????
					*
					* @param phonePeople
					*/
				private void onHandleCallIn(BTPhonePeople phonePeople){
								if(StringUtil.isNotEmpty(phonePeople.getPhoneNumber())){
												String number = phonePeople.getPhoneNumber();
												String name = getPhoneNameByNumber(number);
												sendBtPhoneBroadcast(GocsdkCallback.BT_CALL_IN, name, number);
												BTControler.getInstance().setTalkingInfo(name, number, BTPhonePeople.Type.Callin);
												BTPhonePeople phonePeo = BTControler.getInstance().getPeopleTalking();
												EventPostUtils.Post(new BTContactEvent(BTStatu.CALL_IN_NEXT, phonePeo));

												ShowLogUtil.show("Display the answering call interface");
												Intent intent = new Intent("com.c2p.c2pbtphone.bt.ui.btphone.callin.PhoneInComingActivity");
												intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												intent.putExtra("comingPhoneName", name);
												intent.putExtra("comingPhoneNumber", number);
												startActivity(intent);
								}
				}

				/**
					* ??????bluetooth phone??????
					*/
				public void startBTPhoneActivty(){
								try{
												ShowLogUtil.show("startBTPhoneActivty");
												Intent intent = new Intent(Constant.Action.ACTION_LF_BT_PHONE);
												intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												intent.putExtra("fragment_type", 0);
												intent.putExtra("call_type", "callout");
												startActivity(intent);
								}
								catch(ActivityNotFoundException e){
												LoggerUtil.e(e.toString());
								}
								catch(Exception ex){
												ex.printStackTrace();
								}
				}

				/**
					* ??????????????????????????????
					*/
				public void startResponseContacts(){
								if(null != contactMap && !contactMap.isEmpty()){
												LoggerUtil.e("BTPhoneService  ??????????????? size = " + contactMap.size());
												try{
																Intent intent = new Intent(Constant.Action.ACTION_LF_CAR_SERVICE);
																intent.setPackage("com.lingfei.bmw.idfive");
																intent.putExtra("cmd_type", Constant.Voice.RESPONSE_CONTACTS);
																intent.putExtra("contacts", (Serializable) contactMap);
																startService(intent);
												}
												catch(Exception ex){
																ex.printStackTrace();
												}
								}
				}

				/**
					* ???????????????????????????????????????
					*
					* @param event
					*/
				@Subscribe(threadMode = ThreadMode.BackgroundThread)
				public void onEventBTStatuChange(BTStatuEvent event){
								if(null != event){
												switch(event.getStatu()){
																case CONNECTED:{
																				ShowLogUtil.show("Bluetooth connection");
																				setToast("Bluetooth device connection");
																				initServcieData(true);
																				break;
																}
																case DISCONNECTED:{
																				ShowLogUtil.show("bluetooth disconnected");
																				initServcieData(false);
																				BTControler.getInstance().setHfpStatus(GocsdkCallback.HfpStatus_offline);
																				break;
																}
																case ON_LINE:{ // ????????????
																				ShowLogUtil.show("BT HFP connection");
																				BTControler.getInstance().setBTHfpConnected(true);
																				break;
																}
																case OFF_LINE:{ // ????????????
																				ShowLogUtil.show("BT HFP disconnected");
																				BTControler.getInstance().setBTHfpConnected(false);
																				break;
																}
																case CALL_IN:
																case CALL_OUT:{
																				BTControler.getInstance().setHfpStatus(GocsdkCallback.HfpStatus_callout);
																				notifyBTStateTaiking(1);
																				break;
																}
																case TALKING:{
																				BTControler.getInstance().getPeopleTalking().setType(BTPhonePeople.Type.TALKING);
																				BTControler.getInstance().setHfpStatus(GocsdkCallback.HfpStatus_talking);
																				notifyBTStateTaiking(1);
																				break;
																}
																case HANG_UP:{
																				BTControler.getInstance().onResetPeopleTaiking();
																				Handler handler = new Handler(Looper.getMainLooper());
																				handler.postDelayed(new Runnable(){
																								@Override
																								public void run(){
																												notifyBTStateTaiking(0);
																								}
																				}, 2000);

																				BTControler.getInstance().setHfpStatus(GocsdkCallback.HfpStatus_online);
																				abandonAudioFocus();
																				break;
																}
																case ON_TALKING:
																				break;
																case ON_RING_START:{
																				break;
																}
																case ON_RING_STOP:{
																				break;
																}
																default:
																				break;
												}
								}
				}

				/**
					* Handling events related to Bluetooth module contacts
					*
					* @param event
					*/
				@Subscribe(threadMode = ThreadMode.BackgroundThread)
				public void onEventBTContactChange(BTContactEvent event){
								if(null != event){
												switch(event.getStatu()){
																case PHONE_BOOK:{
																				BTPhonePeople phonePeople = event.getPhonePeople();
																				onSaveBTPhonePeople(phonePeople);
																				break;
																}
																case CALL_OUT:{
																				requestAudioFocus();
																				// tell the mcu to make a call
																				BTPhoneCmd.sendBTPhoneCmd(BTPhoneCmd.CALL_OUT);
																				BTPhonePeople phonePeople = event.getPhonePeople();
																				if(null != phonePeople){
																								String number = phonePeople.getPhoneNumber();
																								String name = getPhoneNameByNumber(number);
																								// make a call
																								sendBtPhoneBroadcast(GocsdkCallback.BT_CALL_OUT, name, number);
																								BTControler.getInstance().setTalkingInfo(name, number, BTPhonePeople.Type.CALL_OUT);
																								EventPostUtils.Post(new BTContactEvent(BTStatu.CALL_OUT_NEXT, BTControler.getInstance().getPeopleTalking()));
																								startBTPhoneActivty();

																								// During a call, solve the problem that there is no dial tone when dialing
																								sendBtPhoneBroadcast(GocsdkCallback.BT_TALKING, "", "");
																				}
																				break;
																}
																case PHONE_BOOK_DONE:{
																				List<BTPhonePeople> phonePeoples = new ArrayList<>(phoneBookMap.values());
																				// Sort the collection
																				Collections.sort(phonePeoples, new Comparator<BTPhonePeople>(){
																								@Override
																								public int compare(BTPhonePeople lhs, BTPhonePeople rhs){
																												// Sort by name
																												return lhs.getPeopleName().compareTo(rhs.getPeopleName());
																								}
																				});

																				BTControler.getInstance().setPhonePeoples(phonePeoples);
																				EventPostUtils.Post(new BTSearchContactDoneEvent(phonePeoples));
																				isPhoneBookDone = true;
																				startResponseContacts(); // Sync contacts to peers
																				break;
																}
																case CALL_IN:{
																				requestAudioFocus();
																				ShowLogUtil.show("incoming call");
																				BTPhonePeople phonePeople = event.getPhonePeople();
																				onHandleCallIn(phonePeople);
																				break;
																}
																default:
																				break;
												}
								}
				}

				/**
					* ?????????????????????????????????
					*
					* @param event
					*/
				@Subscribe(threadMode = ThreadMode.BackgroundThread)
				public void onEventBTDeviceChange(BTDeviceEvent event){
								if(null != event){
												switch(event.getStatu()){
																case CURRENT_ADDR:{
																				BTDevice device = event.getDevice();
																				if(StringUtil.isNotEmpty(device.getDeviceAddr())){
																								checkCurrentBTDeviceAddr(device);
																				}
																				break;
																}
																case CURRENT_DEVICE_NAME:{
																				BTDevice device = event.getDevice();
																				if(null != device && StringUtil.isNotEmpty(device.getDeviceName())){
																								// ???????????????????????????
																								BTControler.getInstance().saveLocalBtName(device.getDeviceName());
																				}
																				break;
																}
																default:
																				break;
												}
								}
				}

				/**
					* ??????????????????????????????
					*
					* @param event
					*/
				@Subscribe(threadMode = ThreadMode.BackgroundThread)
				public void onEventBTMusicInfo(BTMusicEvent event){
								if(null != event){
												ShowLogUtil.show(" bluetooth music " + event.getStatu().name());
												switch(event.getStatu()){
																case MUSIC_INFO:{
																				BTMusic music = event.getMusic();
																				if(null != music){
																								ShowLogUtil.show(" music name = " + music.musicName + " + artist = " + music.artist);
																				}
																				break;
																}
																case MUSIC_PLAY:{
																				// pause listening
																				SwitchUtil.doStartLingFeiCarService(Constant.Voice.PAUSE_TXT_MUSIC, null);
																				SystemProperties.set("net.btmusice_status", "true");
																				break;
																}
																case MUSIC_STOP:{
																				SystemProperties.set("net.btmusice_status", "false");
																}
																default:
																				break;

												}
								}
				}

				@Override
				public void onCreate(){
								super.onCreate();
								EventBus.getDefault().register(this);
								initAudioManage();
								ShowLogUtil.show("  BTPhoneService  onCreate");
								mToken = BTControler.getInstance().bindToService(this);
				}

				@Override
				public IBinder onBind(Intent intent){
								return null;
				}

				@Override
				public int onStartCommand(Intent intent, int flags, int startId){
								if(null != intent){
												String command = intent.getStringExtra("command");
												ShowLogUtil.show("  BTPhoneService  command = " + command);
												if(StringUtil.isNotEmpty(command)){
																switch(command){
																				case Constant.CMD_PAUSE_BTMUSIC:
																								// Pause music
																								BTControler.getInstance().setMusicPause();
																								EventPostUtils.Post(new FinishBTMusicEvent()); // Exit the Bluetooth music interface at the same time
																								break;
																				case Constant.CMD_PLAY_BTMUSIC:
																								BTControler.getInstance().setMusicPlayOrPause();
																								break;
																				case Constant.Voice.CMD_BT_REQUEST_CONTACTS:
																								if(isPhoneBookDone){
																												startResponseContacts();
																								}
																								break;
																				case Constant.Voice.CMD_BT_DIAL:
																								String number = intent.getStringExtra("number");
																								String name = intent.getStringExtra("name");
																								if(StringUtil.isNotEmpty(number)){
																												BTControler.getInstance().toCall(number);
																								}
																								break;
																				case Constant.Voice.CMD_BT_ANSWER:
																								BTControler.getInstance().toAnswer();
																								break;
																				case Constant.Voice.CMD_BT_REJECT:
																				case Constant.Voice.CMD_BT_HANG_UP:
																								BTControler.getInstance().toHangup();
																								break;
																				default:
																								break;
																}
												}
								}

								return super.onStartCommand(intent, flags, startId);
				}

				@Override
				public void onDestroy(){
								super.onDestroy();
								BTControler.getInstance().unBindFromService(mToken);
								// Send a broadcast to restart the service
								sendBroadcast(new Intent(Constant.Action.ACTION_RESET_BT_PHONE_SERVICE));
								EventBus.getDefault().unregister(this);
				}
}
