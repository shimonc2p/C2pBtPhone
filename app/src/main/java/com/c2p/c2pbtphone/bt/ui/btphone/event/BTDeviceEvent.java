package com.c2p.c2pbtphone.bt.ui.btphone.event;

import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTDevice;
import com.c2p.c2pbtphone.bt.ui.btphone.util.BTStatu;

/**
	* BTDeviceEvent
	*
	* @author heyu
	* @date 2017/6/23.
	*/
public class BTDeviceEvent extends BTStatuEvent{
				public BTDeviceEvent(BTStatu statu, BTDevice device){
								super(statu, device);
				}
}
