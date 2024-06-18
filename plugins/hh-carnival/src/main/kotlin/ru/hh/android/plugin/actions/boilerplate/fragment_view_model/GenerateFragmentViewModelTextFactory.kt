package ru.hh.android.plugin.actions.boilerplate.fragment_view_model

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class GenerateFragmentViewModelTextFactory {

    companion object {
        private const val TOOTHPICK_INJECT_CONSTRUCTOR_FQCN = "toothpick.InjectConstructor"
        private const val TIMBER_FQCN = "timber.log.Timber"
        private const val RX_JAVA_OBSERVABLE_FQCN = "io.reactivex.Observable"

        private const val HH_MVI_VIEW_MODEL_FQCN = "ru.hh.android.mvvm.viewmodel.MviViewModel"

        fun getInstance(project: Project): GenerateFragmentViewModelTextFactory = project.service()
    }

    fun getSingleEventClassText(names: GenerateFragmentViewModelNames): String {
        return """
        ${packageDirective(names.modelsPackageName)}
            
        internal interface ${names.singleEventClassName}     
        """
    }

    fun getUiStateClassText(names: GenerateFragmentViewModelNames): String {
        return """
        ${packageDirective(names.modelsPackageName)}  
            
        internal data class ${names.uiStateClassName}(
            // TODO - add data class content
        )    
        """
    }

    fun getUiStateConverterClassText(
        names: GenerateFragmentViewModelNames,
    ): String {
        return """
        ${packageDirective(names.modelsPackageName)}    
            
        @$TOOTHPICK_INJECT_CONSTRUCTOR_FQCN    
        internal class ${names.uiStateConverterClassName} {
        
            fun convert(item: ${names.mviFeatureStateClassFQCN}): ${names.uiStateClassName} {
                TODO("Implement converter")
            }
        }    
        """
    }

    fun getViewModelPropertyText(
        names: GenerateFragmentViewModelNames,
    ): String {
        return """
        private val viewModel: ${names.viewModelClassName} by stateViewModel(
            handleEvent = this::handleEvent,
            renderState = this::renderState,
            viewModelProvider = { di.getInstance() }
        )    
        """
    }

    fun getHandleEventMethodText(
        names: GenerateFragmentViewModelNames,
    ): String {
        val logText = "$TIMBER_FQCN.tag(LOG_TAG).d(\"handleEvent() called with: event = \$event\")"
        return """
        private fun handleEvent(event: ${names.singleEventClassFQCN}) {
            $logText
        }    
        """
    }

    fun getRenderStateMethodText(
        names: GenerateFragmentViewModelNames,
    ): String {
        val logText = "$TIMBER_FQCN.tag(LOG_TAG).d(\"renderState() called with: state = \$state\")"
        return """
        private fun renderState(state: ${names.uiStateClassFQCN}) {
            $logText
        }    
        """
    }

    @Suppress("detekt.MaxLineLength")
    fun getViewModelClassText(
        names: GenerateFragmentViewModelNames,
    ): String {
        val logNewsText = "$TIMBER_FQCN.tag(LOG_TAG).d(\"processNews() called with: news = \$news\")"

        return """
        ${packageDirective(names.packageName)}    
            
        @$TOOTHPICK_INJECT_CONSTRUCTOR_FQCN
        internal class ${names.viewModelClassName}(
            private val feature: ${names.mviFeatureClassFQCN},
            private val uiConverter: ${names.uiStateConverterClassFQCN},
        ): $HH_MVI_VIEW_MODEL_FQCN<${names.singleEventClassFQCN}, ${names.uiStateClassFQCN}, ${names.mviFeatureStateClassFQCN}, ${names.mviFeatureNewsClassFQCN}>() {
        
            companion object {
                private const val LOG_TAG = "${names.viewModelClassName}"
            }
        
            override val featureStateObservable: $RX_JAVA_OBSERVABLE_FQCN<${names.mviFeatureStateClassFQCN}> = $RX_JAVA_OBSERVABLE_FQCN.wrap(feature)
            override val featureNewsObservable: $RX_JAVA_OBSERVABLE_FQCN<${names.mviFeatureNewsClassFQCN}> = $RX_JAVA_OBSERVABLE_FQCN.wrap(feature.news)
        
            override val uiStateConverter: (${names.mviFeatureStateClassFQCN}) -> ${names.uiStateClassFQCN} = { uiConverter.convert(it) }
        
            override fun processNews(news: ${names.mviFeatureNewsClassFQCN}) {
                $logNewsText
            }
            
        }    
        """
    }

    private fun packageDirective(packageName: String): String {
        return "package $packageName"
    }
}
