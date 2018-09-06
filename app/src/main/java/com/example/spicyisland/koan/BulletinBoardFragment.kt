package com.example.spicyisland.koan

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spicyisland.koan.databinding.FragmentBulletinBoardBinding

/**
 * TODO: receivedStuffs.receivedStrings.value.get("bulletinBoard")から取得したものをbinding.bulletinBoardに代入する処理を書く
 */
class BulletinBoardFragment : Fragment() {

    private lateinit var binding: FragmentBulletinBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bulletin_board, container, false)
        binding.receivedStuffs = receivedStuffs
        binding.setLifecycleOwner(this)

        return binding.root
    }


}
