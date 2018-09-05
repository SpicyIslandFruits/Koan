package com.example.spicyisland.koan

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * TODO: receivedStuffs.receivedStrings.value.get("bulletinBoard")から取得したものをbinding.bulletinBoardに代入する処理を書く
 */
class BulletinBoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_bulletin_board, container, false)
    }


}
