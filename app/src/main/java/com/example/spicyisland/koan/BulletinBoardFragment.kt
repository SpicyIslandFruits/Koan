package com.example.spicyisland.koan

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spicyisland.koan.databinding.FragmentBulletinBoardBinding

/**
 * receivedStuffs.receivedBulletinBoardLinks.valueに取ってきた時間割が入るのでそれをバインディングに代入している
 * TODO: レイアウトの方に上の変数がnullだったときリンクを押したらリンク取得中と通知を出す処理を書く
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
