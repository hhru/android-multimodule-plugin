package ru.hh.android.plugins.garcon.model


enum class KakaoViewDeclaration(
    val fqn: String,
    val androidWidgetsClasses: List<String>
) {

    BOTTOM_NAVIGATION_VIEW(
        fqn = "com.agoda.kakao.bottomnav.KBottomNavigationView",
        androidWidgetsClasses = listOf(
            "com.google.android.material.bottomnavigation.BottomNavigationView"
        )
    ),
    CHECKBOX(
        fqn = "com.agoda.kakao.check.KCheckBox",
        androidWidgetsClasses = listOf(
            "android.widget.CheckBox",
            "androidx.appcompat.widget.AppCompatCheckBox"
        )
    ),
    BUTTON(
        fqn = "com.agoda.kakao.text.KButton",
        androidWidgetsClasses = listOf(
            "android.widget.Button",
            "androidx.appcompat.widget.AppCompatButton"
        )
    ),
    DATE_PICKER(
        fqn = "com.agoda.kakao.picker.date.KDatePicker",
        androidWidgetsClasses = listOf(
            "android.widget.DatePicker"
        )
    ),
    DRAWER_VIEW(
        fqn = "com.agoda.kakao.drawer.KDrawerView",
        androidWidgetsClasses = listOf(
            "androidx.drawerlayout.widget.DrawerLayout"
        )
    ),
    EDIT_TEXT(
        fqn = "com.agoda.kakao.edit.KEditText",
        androidWidgetsClasses = listOf(
            "android.widget.EditText",
            "androidx.appcompat.widget.AppCompatEditText"
        )
    ),
    IMAGE_VIEW(
        fqn = "com.agoda.kakao.image.KImageView",
        androidWidgetsClasses = listOf(
            "android.widget.ImageView",
            "androidx.appcompat.widget.AppCompatImageView"
        )
    ),
    NAVIGATION_VIEW(
        fqn = "com.agoda.kakao.navigation.KNavigationView",
        androidWidgetsClasses = listOf(
            "com.google.android.material.navigation.NavigationView"
        )
    ),
    PROGRESS_BAR(
        fqn = "com.agoda.kakao.progress.KProgressBar",
        androidWidgetsClasses = listOf(
            "android.widget.ProgressBar"
        )
    ),
    RATING_BAR(
        fqn = "com.agoda.kakao.rating.KRatingBar",
        androidWidgetsClasses = listOf(
            "android.widget.RatingBar",
            "androidx.appcompat.widget.AppCompatRatingBar"
        )
    ),
    SCROLL_VIEW(
        fqn = "com.agoda.kakao.scroll.KScrollView",
        androidWidgetsClasses = listOf(
            "android.widget.ScrollView",
            "android.widget.HorizontalScrollView",
            "androidx.core.widget.NestedScrollView"
        )
    ),
    SEEK_BAR(
        fqn = "com.agoda.kakao.progress.KSeekBar",
        androidWidgetsClasses = listOf(
            "android.widget.SeekBar",
            "androidx.appcompat.widget.AppCompatSeekBar"
        )
    ),
    SWIPE_REFRESH_LAYOUT(
        fqn = "com.agoda.kakao.swiperefresh.KSwipeRefreshLayout",
        androidWidgetsClasses = listOf(
            "androidx.swiperefreshlayout.widget.SwipeRefreshLayout"
        )
    ),
    SWITCH(
        fqn = "com.agoda.kakao.switch.KSwitch",
        androidWidgetsClasses = listOf(
            "android.widget.Switch",
            "androidx.appcompat.widget.SwitchCompat"
        )
    ),
    TAB_LAYOUT(
        fqn = "com.agoda.kakao.tabs.KTabLayout",
        androidWidgetsClasses = listOf(
            "com.google.android.material.tabs.TabLayout"
        )
    ),
    TEXT_INPUT_LAYOUT(
        fqn = "com.agoda.kakao.edit.KTextInputLayout",
        androidWidgetsClasses = listOf(
            "com.google.android.material.textfield.TextInputLayout",
            "com.google.android.material.textfield.TextInputEditText"
        )
    ),
    TEXT_VIEW(
        fqn = "com.agoda.kakao.text.KTextView",
        androidWidgetsClasses = listOf(
            "android.widget.TextView",
            "androidx.appcompat.widget.AppCompatTextView"
        )
    ),
    TIME_PICKER(
        fqn = "com.agoda.kakao.picker.time.KTimePicker",
        androidWidgetsClasses = listOf(
            "android.widget.TimePicker"
        )
    ),
    RECYCLER_VIEW(
        fqn = "com.agoda.kakao.recycler.KRecyclerView",
        androidWidgetsClasses = listOf(
            "androidx.recyclerview.widget.RecyclerView"
        )
    ),
    VIEW(
        fqn = "com.agoda.kakao.common.views.KView",
        androidWidgetsClasses = listOf(
            "android.view.View"
        )
    ),
    VIEW_PAGER(
        fqn = "com.agoda.kakao.pager.KViewPager",
        androidWidgetsClasses = listOf(
            "androidx.viewpager.widget.ViewPager"
        )
    )

}