package com.example.spicyisland.koan.Fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spicyisland.koan.Activities.WebViewActivity
import com.example.spicyisland.koan.Models.User
import com.example.spicyisland.koan.R
import com.example.spicyisland.koan.databinding.FragmentCurriculumBinding
import com.example.spicyisland.koan.Models.receivedStuffs
import io.realm.Realm

/**
 * TODO: receivedStuffs.receivedStrings.value.get("curriculum")で時間割を取得し、binding.curriculumに代入する
 */

class CurriculumFragment : Fragment() {

    private lateinit var binding: FragmentCurriculumBinding
    private val realm = Realm.getDefaultInstance()
    private val userData = realm.where(User::class.java).findFirst()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_curriculum, container, false)
        binding.receivedStuffs = receivedStuffs
        binding.owner = this
        binding.setLifecycleOwner(this)

        return binding.root
    }

    fun onClickCurriculum(curriculumPosition: Int) {
        val availableCurriculumPositions = userData!!.availableCurriculumPositions
        for ((position, availableCurriculumPosition) in availableCurriculumPositions.withIndex()){
            if (availableCurriculumPosition == curriculumPosition) {
                val intent = Intent(this.context, WebViewActivity::class.java)
                intent.putExtra("LINK", userData.syllabusLinks[position])
                startActivity(intent)
            }
        }
    }

}
