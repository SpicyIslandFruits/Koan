package com.example.spicyisland.koan

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_curriculum.view.*

class CurriculumFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_curriculum, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        KoanService().getStringsObservableCallable(KoanCurriculum, koanCookies, "td", arrayListOf(3, 4, 5, 6, 7, 8, 9, 10))
                .subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<ArrayList<String>>{
                    override fun onComplete() {
                        Log.d("d", "onComplete()")
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d("d", "onSubscribed()")
                    }

                    override fun onNext(t: ArrayList<String>) {

                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
    }
}
