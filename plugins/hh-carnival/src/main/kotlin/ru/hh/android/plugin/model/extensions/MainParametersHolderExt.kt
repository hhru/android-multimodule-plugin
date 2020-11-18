package ru.hh.android.plugin.model.extensions

import ru.hh.android.plugin.model.MainParametersHolder
import ru.hh.android.plugin.model.enums.PredefinedFeature


fun MainParametersHolder.checkFeature(feature: PredefinedFeature) = enabledFeatures.contains(feature)