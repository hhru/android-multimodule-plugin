package ${package_name}.di

<#if need_create_api_interface>
import ${package_name}.${formatted_library_name}Api
import ${package_name}.di.provider.${formatted_library_name}ApiProvider
</#if>
<#if need_create_repository_with_interactor>
import ${package_name}.interactor.${formatted_library_name}Interactor
import ${package_name}.repository.${formatted_library_name}Repository
</#if>
import toothpick.config.Module


class ${formatted_library_name}Module : Module() {

    init {
        <#if need_create_api_interface>
        bind(${formatted_library_name}Api::class.java).toProvider(${formatted_library_name}ApiProvider::class.java).providesSingletonInScope()
        </#if>
        <#if need_create_repository_with_interactor>
        bind(${formatted_library_name}Interactor::class.java).to(${formatted_library_name}Interactor::class.java).singletonInScope()
        bind(${formatted_library_name}Repository::class.java).to(${formatted_library_name}Repository::class.java).singletonInScope()
        </#if>
    }

}