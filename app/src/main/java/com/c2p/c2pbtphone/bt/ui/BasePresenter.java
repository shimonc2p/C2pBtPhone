package com.c2p.c2pbtphone.bt.ui;


import androidx.annotation.NonNull;

public interface BasePresenter<T extends BaseView> {

    void attachView(@NonNull T view);

    void detachView();
}
