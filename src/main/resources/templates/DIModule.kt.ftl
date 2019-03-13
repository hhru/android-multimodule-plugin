package ${package_name}.di.module

<#if need_create_api_interface>
import ${package_name}.data.remote.api.${formatted_library_name}Api
import ${package_name}.di.provider.${formatted_library_name}ApiProvider
</#if>
<#if need_create_repository_with_interactor>
import ${package_name}.domain.interactor.${formatted_library_name}Interactor
import ${package_name}.domain.repository.${formatted_library_name}Repository
<#if need_create_interface_for_repository>
import ${package_name}.domain.repository.${formatted_library_name}RepositoryImpl
</#if>
</#if>
<#if need_create_presentation_layer>
import ${package_name}.presentation.presenter.${formatted_library_name}Presenter
</#if>
import toothpick.config.Module


class ${formatted_library_name}Module : Module() {

    init {
        <#if need_create_api_interface>
        bindApi()
        </#if>
        <#if need_create_repository_with_interactor>
        bindRepositories()
        bindInteractors()
        </#if>
        <#if need_create_presentation_layer>
        bindPresenter()
        </#if>
    }

    <#if need_create_api_interface>
    private fun bindApi() {
        bind(${formatted_library_name}Api::class.java)
            .toProvider(${formatted_library_name}ApiProvider::class.java)
            .providesSingletonInScope()
    }
    </#if>

    <#if need_create_repository_with_interactor>
    private fun bindRepositories() {
        <#if need_create_interface_for_repository>
        bind(${formatted_library_name}Repository::class.java)
            .to(${formatted_library_name}RepositoryImpl::class.java)
            .singletonInScope()
        <#else>
        bind(${formatted_library_name}Repository::class.java).singletonInScope()
        </#if>
    }

    private fun bindInteractors() {
        bind(${formatted_library_name}Interactor::class.java).singletonInScope()
    }
    </#if>

    <#if need_create_presentation_layer>
    private fun bindPresenter() {
        bind(${formatted_library_name}Presenter::class.java).singletonInScope()
    }
    </#if>

}