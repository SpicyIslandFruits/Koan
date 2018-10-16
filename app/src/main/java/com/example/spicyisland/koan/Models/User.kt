package com.example.spicyisland.koan.Models

import io.realm.RealmList
import io.realm.RealmObject

/**
 * userDataはidとパスワード足して暗号化したもの
 * ivは復号化に必要なもの
 * curriculumは時間割の文字列
 */
open class User: RealmObject() {
    var userData = byteArrayOf()
    var iv = byteArrayOf()
    var curriculum = RealmList<String>()
    var syllabusLinks = RealmList<String>()
    var availableCurriculumPositions = RealmList<Int>()
}
