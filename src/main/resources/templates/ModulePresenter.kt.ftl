package ${package_name}.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import ru.hh.android.base.ui.BasePresenter
import ${package_name}.presentation.view.${formatted_library_name}View
import javax.inject.Inject


@InjectViewState
class ${formatted_library_name}Presenter @Inject constructor(

) : BasePresenter<${formatted_library_name}View>() {

    companion object {
        private const val LOG_TAG = "${formatted_library_name}Presenter"
    }



}