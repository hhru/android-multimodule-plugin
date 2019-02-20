package ${package_name}.domain.repository

import javax.inject.Inject

class ${formatted_library_name}Repository<#if need_create_interface_for_repository>Impl</#if> @Inject constructor()<#if need_create_interface_for_repository>: ${formatted_library_name}Repository</#if>