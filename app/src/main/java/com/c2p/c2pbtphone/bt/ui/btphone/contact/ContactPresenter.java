package com.c2p.c2pbtphone.bt.ui.btphone.contact;

import androidx.annotation.NonNull;

import com.c2p.c2pbtphone.bt.injector.PerActivity;
import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTPhonePeople;
import com.c2p.c2pbtphone.bt.ui.btphone.util.BTControler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
	* ContactPresenter
	*
	* @author heyu
	* @date 2017/6/19.
	*/
@PerActivity
public class ContactPresenter implements ContactContract.Presenter{
				private ContactContract.View mView;

				@Inject
				public ContactPresenter(){
				}

				@Override
				public void initData(){
								List<BTPhonePeople> phonePeoples = BTControler.getInstance().getPhonePeoples();
								if(null != phonePeoples && !phonePeoples.isEmpty()){
												mView.updateView(phonePeoples);
								}
								else if(BTControler.getInstance().isBTHfpConnected()){
												BTControler.getInstance().openPhoneBook();
								}
				}

				@Override
				public void attachView(@NonNull ContactContract.View view){
								this.mView = view;
				}

				@Override
				public void detachView(){
								this.mView = null;
				}
}
