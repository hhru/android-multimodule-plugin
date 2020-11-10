package ru.hh.android.plugins.garcon.model.mapping

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals


class PropertyNameFetcherTest {

    companion object {

        private const val FILE_NAME = "activity_main.xml"
        private val FILE_PREFIX = FILE_NAME.removeSuffix(".xml")

        @JvmStatic
        fun factoryMethod(): Stream<Arguments> = Stream.of(
            arguments("", ""),
            arguments(FILE_PREFIX, FILE_PREFIX),
            arguments("button", "button"),
            arguments("${FILE_PREFIX}_button_title", "titleButton"),
            arguments("${FILE_PREFIX}_text_view_super_description", "superDescriptionTextView"),
            arguments("${FILE_PREFIX}_recycler_content", "contentRecycler"),
            arguments("${FILE_PREFIX}_edit_text_qwerty", "qwertyEditText"),
            arguments("${FILE_PREFIX}_image_avatar", "avatarImage"),
            arguments("${FILE_PREFIX}_container_roman", "romanContainer"),
            arguments("${FILE_PREFIX}_view_some_thing", "someThingView"),
            arguments("_button_", "button"),
            arguments("${FILE_PREFIX}_button", "button"),
            arguments("${FILE_PREFIX}_title", "${FILE_PREFIX}_title"),
            arguments("${FILE_PREFIX}_button_button", "button"),
            arguments("${FILE_PREFIX}_title_button", "titleButton"),
            arguments("${FILE_PREFIX}_recycler_content_recycler", "contentRecycler"),
            arguments("${FILE_PREFIX}_screen_container_scroll", "screenScrollContainer"),
            arguments("${FILE_PREFIX}_quick_query_text_view_title", "quickQueryTitleTextView"),
            arguments("${FILE_PREFIX}_expandable_title_image_icon", "expandableTitleIconImage"),
            arguments("${FILE_PREFIX}__button__reload", "reloadButton"),
            arguments("${FILE_PREFIX}__button__reload_button", "reloadButton"),
            arguments("${FILE_PREFIX}__text_view", "textView")
        )
    }

    private lateinit var propertyNameFetcher: PropertyNameFetcher

    @BeforeEach
    fun beforeEachTest() {
        propertyNameFetcher = PropertyNameFetcher()
    }


    @ParameterizedTest(name = "Should convert {0} to {1}")
    @MethodSource("factoryMethod")
    fun testPropertyNameFetcher(viewId: String, result: String) {
        assertEquals(result, propertyNameFetcher.getPropertyName(FILE_NAME, viewId))
    }

}