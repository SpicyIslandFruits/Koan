package com.mk.spicyisland.koan.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * LiveDataとして扱うためのクラス
 */
class ReceivedStuffs: ViewModel(){
    val receivedCurriculum = MutableLiveData<MutableList<String>>()
    val receivedBulletinBoardLinks = MutableLiveData<MutableList<String>>()
    val receivedBulletinBoardUnreadCount = MutableLiveData<MutableList<String>>()
    val receivedSyllabusLinks = MutableLiveData<MutableList<String>>()
}

/**
 * ReceivedStuffsのインスタンスを保管しておく変数
 * ViewModelProviders.of(this).get(ReceivedStuffs::class.java)
 * これでインスタンスを取得できる
 * 値を変えたい場合はreceivedStuffs.receivedStrings.value = MutableMap<String, ArrayList<String>>()という感じ
 */
lateinit var receivedStuffs: ReceivedStuffs