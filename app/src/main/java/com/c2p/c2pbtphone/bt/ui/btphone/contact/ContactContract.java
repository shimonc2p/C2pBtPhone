package com.c2p.c2pbtphone.bt.ui.btphone.contact;

import com.c2p.c2pbtphone.bt.ui.BasePresenter;
import com.c2p.c2pbtphone.bt.ui.BaseView;
import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTPhonePeople;

import java.util.List;

/**
	* ContactContract
	*
	* @author heyu
	* @date 2017/6/19.
	*/
public interface ContactContract{
				interface View extends BaseView{
								void updateView(List<BTPhonePeople> phonePeoples);
				}

				interface Presenter extends BasePresenter<View>{
								void initData();
				}
}
