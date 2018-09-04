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
 * koanのホストのurl
 */
const val KoanUrl = "https://koan.osaka-u.ac.jp/"