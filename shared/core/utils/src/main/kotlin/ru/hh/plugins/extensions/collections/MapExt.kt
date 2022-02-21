package ru.hh.plugins.extensions.collections

inline fun <K, V> Map<K, V>.firstOrNull(predicate: (Map.Entry<K, V>) -> Boolean): Map.Entry<K, V>? {
    for (entry in this) {
        if (predicate.invoke(entry)) {
            return entry
        }
    }

    return null
}
