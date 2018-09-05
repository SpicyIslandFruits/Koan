package com.example.spicyisland.koan

/**
 * .followRedirects(false)をセットしてkoanCookiesを取得する
 */
const val KoanMainPage = "https://koan.osaka-u.ac.jp/campusweb/campussmart.do?page=main"

/**
 * 手動でリダイレクトする
 */
const val KoanSsoLoginPage = "https://koan.osaka-u.ac.jp/campusweb/ssologin.do?page=smart"

/**
 * idpCookiesをセットしてユーザーネームとパスワードをポスト
 */
const val IdpAuthnPwd = "https://ou-idp.auth.osaka-u.ac.jp/idp/authnPwd"

/**
 * idpCookiesをセットして{role: self_0}をポスト
 */
const val IdpRoleSelect = "https://ou-idp.auth.osaka-u.ac.jp/idp/roleselect"

/**
 * koanCookiesをセットしてRoleSelectからのレスポンスに含まれるSAMLResponseとRelayStateをポスト
 */
const val KoanSaml2Post = "https://koan.osaka-u.ac.jp/Shibboleth.sso/SAML2/POST"

/**
 * これ以降はkoanCookiesをセットする
 */
const val KoanCurriculum = "https://koan.osaka-u.ac.jp/campusweb/campussmart.do?action=rfw&_flowId=RSW0001000-flow&page=main&tabId=rs&wfId=RSW0001000-flow"

/**
 * koanのホストのurlとスラッシュ抜いたやつ
 */
const val KoanUrl = "https://koan.osaka-u.ac.jp/"
const val KoanUrlWithoutSlash = "https://koan.osaka-u.ac.jp"

/**
 * 掲示板のurl
 */
const val BulletinBoardLink = "https://koan.osaka-u.ac.jp/campusweb/campussmart.do?action=rfw&_flowId=KJW0001100-flow&page=main&tabId=kj&wfId=KJW0001100-flow"

/**
 * 掲示板のここに掲示のurlの末尾
 * KoanUrlWithoutSlashにflowExecutionKey足してからこれを最後に付け加える
 * 以下同様
 */
const val LessonBulletinBulletinLink = "&_eventId=displayJugyo"
const val LessonBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=1&genrecd=1"
const val NotificationBulletinLink = "&_eventId=displayOshirase"
const val IndividualBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=3&genrecd=2"
const val StudentAffairsOfficeBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=342"
const val MinorCourseBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=344"
const val TeachingProfessionBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=345"
const val ScholarshipBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=343"
const val CareerBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=346"
const val SchoolLifeBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=347"
const val StudyAbroadStudentBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=348"
const val StudyAbroadBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=349"
const val OtherBulletinLink = "&_eventId=dispKeijiListGenre&keijitype=4&genrecd=350"
