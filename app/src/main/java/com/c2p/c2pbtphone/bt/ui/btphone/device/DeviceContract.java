package com.c2p.c2pbtphone.bt.ui.btphone.device;

import com.c2p.c2pbtphone.bt.ui.BasePresenter;
import com.c2p.c2pbtphone.bt.ui.BaseView;
import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTDevice;

import java.util.ArrayList;

/**
	* DeviceContract
	*
	* @author heyu
	* @date 2017/6/20.
	*/
public interface DeviceContract{
				interface View extends BaseView{
								void updateView(ArrayList<BTDevice> list);
				}

				interface Presenter extends BasePresenter<View>{
								void initData();
				}
}
