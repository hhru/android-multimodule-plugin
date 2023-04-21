#!/bin/bash

source infra/constants.sh
source infra/utils.sh


readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")

logMessage "Build all plugins..."

bash gradlew \
  clean \
  buildAllPlugins \
  collectUpdatePluginsXmlTask \
  --customRepositoryUrl="${repoUrl}" \
  --no-configuration-cache

logMessage "Successfully build all plugins"