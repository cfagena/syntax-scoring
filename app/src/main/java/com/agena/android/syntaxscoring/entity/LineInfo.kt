package com.agena.android.syntaxscoring.entity

data class LineInfo(
    val result: LineResult? = null,
    val score: Int = -1,
    val remark: String? = null
)
