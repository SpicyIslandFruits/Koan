package com.example.spicyisland.koan.Fragments

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.spicyisland.koan.R
import com.example.spicyisland.koan.Activities.WebViewActivity
import com.example.spicyisland.koan.databinding.FragmentBulletinBoardBinding
import com.example.spicyisland.koan.Models.receivedStuffs
import kotlinx.android.synthetic.main.fragment_bulletin_board.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        lesson_bulletin_bulletin.setOnClickListener{
            openWebView(0)
        }

        lesson_bulletin.setOnClickListener{
            openWebView(1)
        }

        notification_bulletin.setOnClickListener{
            openWebView(2)
        }

        individual_bulletin.setOnClickListener{
            openWebView(3)
        }

        student_affairs_office_bulletin.setOnClickListener{
            openWebView(4)
        }

        minor_course_bulletin.setOnClickListener{
            openWebView(5)
        }

        teaching_profession_bulletin.setOnClickListener{
            openWebView(6)
        }

        scholarship_bulletin.setOnClickListener{
            openWebView(7)
        }

        career_bulletin.setOnClickListener{
            openWebView(8)
        }

        school_life_bulletin.setOnClickListener{
            openWebView(9)
        }

        study_abroad_student_bulletin.setOnClickListener{
            openWebView(10)
        }

        study_abroad_bulletin.setOnClickListener{
            openWebView(11)
        }

        other_bulletin.setOnClickListener{
            openWebView(12)
        }

    }

    /**
     * TODO: ページの有効期限切れになるのでなんとかする
     * TODO: onStartでリンクの再取得をするため古いリンクが使えなくなるから
     */
    private fun openWebView(linkNum: Int) {
        if (receivedStuffs.receivedBulletinBoardLinks.value != null) {
            val intent = Intent(this.context, WebViewActivity::class.java)
            intent.putExtra("LINK", receivedStuffs.receivedBulletinBoardLinks.value!![linkNum])
            startActivity(intent)
        } else {
            Toast.makeText(this.context, R.string.link_not_set_yet, Toast.LENGTH_LONG).show()
        }
    }
}
