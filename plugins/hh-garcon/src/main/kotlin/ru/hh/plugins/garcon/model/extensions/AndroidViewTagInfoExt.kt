package ru.hh.plugins.garcon.model.extensions

import ru.hh.plugins.garcon.extensions.psi.rFilePackageName
import ru.hh.plugins.garcon.model.AndroidViewTagInfo


val AndroidViewTagInfo.xmlFileName: String get() = this.xmlFile.name

val AndroidViewTagInfo.rFilePackageName: String get() = this.xmlFile.rFilePackageName
