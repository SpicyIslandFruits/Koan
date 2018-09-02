package com.example.spicyisland.koan

import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmList
import kotlinx.android.synthetic.main.fragment_curriculum.*

var isConnecting = false

class CurriculumFragment : Fragment() {

    val realm = Realm.getDefaultInstance()!!
    private val userData = realm.where(User::class.java).findFirst()
    private val koanCookies = KoanService.getCookieMapFromCookieManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_curriculum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userData != null && userData.curriculum.size >= 36)
            setTexts(userData.curriculum)
        else {
            curriculumLayout.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
        }

        if (userData != null && !isConnecting)
            getAndSaveCurriculum()

        if (userData == null)
            startActivity(Intent(this.context, StartActivity::class.java))

    }

    private fun getAndSaveCurriculum(){

        KoanService.getStringsObservableCallableFromTagAndTagPosition(KoanCurriculum, koanCookies,
                "td", curriculumTagPositions)
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<MutableList<String>> {
                    val realm = Realm.getDefaultInstance()
                    val userData = realm.where(User::class.java).findFirst()

                    override fun onComplete() {
                        try {
                            progressBar.visibility = View.GONE
                            curriculumLayout.visibility = View.VISIBLE
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                        realm.close()
                    }

                    override fun onSubscribe(d: Disposable) {
                        isConnecting = true
                    }

                    override fun onNext(curriculums: MutableList<String>) {
                        val realmCurriculum = RealmList<String>()
                        for (curriculum in curriculums)
                            realmCurriculum.add(curriculum)

                        realm.beginTransaction()
                        userData!!.curriculum = realmCurriculum
                        realm.commitTransaction()

                        //subscribeが終わる前にフラグメントが破棄された場合はsetTextsが呼べないため何もしない
                        try {
                            setTexts(userData.curriculum)
                        }catch (e: IllegalStateException){
                            e.printStackTrace()
                        }
                        isConnecting = false
                    }

                    override fun onError(e: Throwable) {
                        //アプリ内ブラウザからログアウトしたり、cookieの有効期限が切れた場合は自動でcookieを再取得する
                        e.printStackTrace()
                        isConnecting = false
                        RecoverCookies()
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun setTexts(curriculum: MutableList<String>) {

        firstMonday.text = curriculum[0]
        if(curriculum[0] != "")
            firstMonday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        firstTuesday.text = curriculum[1]
        if(curriculum[1] != "")
            firstTuesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        firstWednesday.text = curriculum[2]
        if(curriculum[2] != "")
            firstWednesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        firstThursday.text = curriculum[3]
        if(curriculum[3] != "")
            firstThursday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        firstFriday.text = curriculum[4]
        if(curriculum[4] != "")
            firstFriday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        firstSaturday.text = curriculum[5]
        if(curriculum[5] != "")
            firstSaturday.setBackgroundResource(R.drawable.rounded_corner_background_green)

        secondMonday.text = curriculum[6]
        if(curriculum[6] != "")
            secondMonday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        secondTuesday.text = curriculum[7]
        if(curriculum[7] != "")
            secondTuesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        secondWednesday.text = curriculum[8]
        if(curriculum[8] != "")
            secondWednesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        secondThursday.text = curriculum[9]
        if(curriculum[9] != "")
            secondThursday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        secondFriday.text = curriculum[10]
        if(curriculum[10] != "")
            secondFriday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        secondSaturday.text = curriculum[11]
        if(curriculum[11] != "")
            secondSaturday.setBackgroundResource(R.drawable.rounded_corner_background_green)

        thirdMonday.text = curriculum[12]
        if(curriculum[12] != "")
            thirdMonday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        thirdTuesday.text = curriculum[13]
        if(curriculum[13] != "")
            thirdTuesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        thirdWednesday.text = curriculum[14]
        if(curriculum[14] != "")
            thirdWednesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        thirdThursday.text = curriculum[15]
        if(curriculum[15] != "")
            thirdThursday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        thirdFriday.text = curriculum[16]
        if(curriculum[16] != "")
            thirdFriday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        thirdSaturday.text = curriculum[17]
        if(curriculum[17] != "")
            thirdSaturday.setBackgroundResource(R.drawable.rounded_corner_background_green)

        fourthMonday.text = curriculum[18]
        if(curriculum[18] != "")
            fourthMonday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fourthTuesday.text = curriculum[19]
        if(curriculum[19] != "")
            fourthTuesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fourthWednesday.text = curriculum[20]
        if(curriculum[20] != "")
            fourthWednesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fourthThursday.text = curriculum[21]
        if(curriculum[21] != "")
            fourthThursday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fourthFriday.text = curriculum[22]
        if(curriculum[22] != "")
            fourthFriday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fourthSaturday.text = curriculum[23]
        if(curriculum[23] != "")
            fourthSaturday.setBackgroundResource(R.drawable.rounded_corner_background_green)

        fifthMonday.text = curriculum[24]
        if(curriculum[24] != "")
            fifthMonday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fifthTuesday.text = curriculum[25]
        if(curriculum[25] != "")
            fifthTuesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fifthWednesday.text = curriculum[26]
        if(curriculum[26] != "")
            fifthWednesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fifthThursday.text = curriculum[27]
        if(curriculum[27] != "")
            fifthThursday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fifthFriday.text = curriculum[28]
        if(curriculum[28] != "")
            fifthFriday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        fifthSaturday.text = curriculum[29]
        if(curriculum[29] != "")
            fifthSaturday.setBackgroundResource(R.drawable.rounded_corner_background_green)

        sixthMonday.text = curriculum[30]
        if(curriculum[30] != "")
            sixthMonday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        sixthTuesday.text = curriculum[31]
        if(curriculum[31] != "")
            sixthTuesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        sixthWednesday.text = curriculum[32]
        if(curriculum[32] != "")
            sixthWednesday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        sixthThursday.text = curriculum[33]
        if(curriculum[33] != "")
            sixthThursday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        sixthFriday.text = curriculum[34]
        if(curriculum[34] != "")
            sixthFriday.setBackgroundResource(R.drawable.rounded_corner_background_green)
        sixthSaturday.text = curriculum[35]
        if(curriculum[35] != "")
            sixthSaturday.setBackgroundResource(R.drawable.rounded_corner_background_green)
    }
}
