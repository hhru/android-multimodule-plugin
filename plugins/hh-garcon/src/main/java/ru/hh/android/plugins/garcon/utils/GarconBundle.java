package ru.hh.android.plugins.garcon.utils;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;


/**
 * Utility class for getting messages from resource-bundle.
 * <p>
 * - Don't know why this don't compile in /kotlin source folder.
 * - Don't know why this {@link CommonBundle#message(String, Object...)} stuff don't properly work with Kotlin.
 */
public final class GarconBundle {

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return CommonBundle.message(getBundle(), key, params);
    }

    @NotNull
    public static String message(@PropertyKey(resourceBundle = BUNDLE) String key) {
        return CommonBundle.message(getBundle(), key);
    }


    private static Reference<ResourceBundle> ourBundle;
    @NonNls
    private static final String BUNDLE = "messages.GarconBundle";

    private GarconBundle() {
        // to prevent creation
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = com.intellij.reference.SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            ourBundle = new SoftReference<>(bundle);
        }
        return bundle;
    }

}
