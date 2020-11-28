#!/bin/bash

source infra/constants.sh
source infra/utils.sh


logMessage "Push plugins to repository..."

readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")
readonly repoCredentials=$(prop "${PROPERTY_PLUGINS_REPO_CREDENTIALS}" "${PLUGINS_PROPERTIES_FILE}")

putItemIntoRepo "${repoUrl}" "${repoCredentials}" "./build/${UPDATE_PLUGINS_XML_FILE_NAME}"
for pluginName in ${PLUGINS_DIRS_NAMES[@]}; do
  putItemIntoRepo "${repoUrl}" "${repoCredentials}" "./plugins/${pluginName}/build/distributions/${pluginName}.zip"
done

logMessage "Successfully pushed every plugin distribution to repository"