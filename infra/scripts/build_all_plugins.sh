#!/bin/bash

source infra/constants.sh
source infra/utils.sh


readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")

logMessage "Build all plugins..."

bash gradlew buildAllPlugins collectUpdatePluginsXmlTask --customRepositoryUrl="${repoUrl}"

logMessage "Successfully build all plugins"