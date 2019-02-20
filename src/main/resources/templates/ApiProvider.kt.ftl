package ${package_name}.di.provider

import ${package_name}.data.remote.api.${formatted_library_name}Api
import ru.hh.network_auth_source.AuthZoneApiFactory
import ru.hh.network_source.ServerUrl
import javax.inject.Inject
import javax.inject.Provider


class ${formatted_library_name}ApiProvider @Inject constructor(
        private val authZoneApiFactory: AuthZoneApiFactory,
        private val serverUrl: ServerUrl
) : Provider<${formatted_library_name}Api> {

    override fun get(): ${formatted_library_name}Api {
        return authZoneApiFactory.createAuthZone(${formatted_library_name}Api::class.java, serverUrl)
    }

}