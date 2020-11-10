package ru.hh.android.plugins.garcon.model.page_object

import ru.hh.android.plugins.garcon.model.KakaoViewDeclaration


data class PageObjectProperty(
    val id: String,
    val xmlFileName: String,
    val kakaoViewDeclaration: KakaoViewDeclaration
)