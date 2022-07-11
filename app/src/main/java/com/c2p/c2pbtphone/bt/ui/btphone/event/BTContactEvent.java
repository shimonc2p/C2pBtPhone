package com.c2p.c2pbtphone.bt.ui.btphone.event;

import com.c2p.c2pbtphone.bt.ui.btphone.bean.BTPhonePeople;
import com.c2p.c2pbtphone.bt.ui.btphone.util.BTStatu;

/**
	* BTContactEvent
	* 与联系人相关的事件
	* @author heyu
	* @date 2017/6/23.
	*/
public class BTContactEvent extends BTStatuEvent{
				public BTContactEvent(BTStatu statu, BTPhonePeople phonePeople){
								super(statu, phonePeople);
				}
}
