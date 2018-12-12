package ru.hh.android.core

abstract class BasePresenter<T : BaseView> {

    protected lateinit var view: T


    fun bindView(view: T) {
        this.view = view
    }


    open fun onCreate() {
        // nothing to do by default.
    }

    open fun onDestroy() {
        // nothing to do by default.
    }

}