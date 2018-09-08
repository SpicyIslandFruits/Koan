package com.example.spicyisland.koan.Models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * LiveDataとして扱うためのクラス
 */
class ReceivedStuffs: ViewModel(){
    val receivedCurriculum = MutableLiveData<MutableList<String>>()
    val receivedBulletinBoardLinks = MutableLiveData<MutableList<String>>()
    val receivedBulletinBoardUnreadCount = MutableLiveData<MutableList<String>>()
}

/**
 * ReceivedStuffsのインスタンスを保管しておく変数
 * ViewModelProviders.of(this).get(ReceivedStuffs::class.java)
 * これでインスタンスを取得できる
 * 値を変えたい場合はreceivedStuffs.receivedStrings.value = MutableMap<String, ArrayList<String>>()という感じ
 */
lateinit var receivedStuffs: ReceivedStuffs