package com.example.spicyisland.koan

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spicyisland.koan.databinding.FragmentCurriculumBinding

/**
 * TODO: receivedStuffs.receivedStrings.value.get("curriculum")で時間割を取得し、binding.curriculumに代入する
 */

class CurriculumFragment : Fragment() {

    private lateinit var binding: FragmentCurriculumBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_curriculum, container, false)
        binding.receivedStuffs = receivedStuffs
        binding.setLifecycleOwner(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
