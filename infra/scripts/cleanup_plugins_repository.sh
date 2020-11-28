#!/bin/bash

source infra/constants.sh
source infra/utils.sh


logMessage "Cleanup plugins repository..."

readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")
readonly repoCredentials=$(prop "${PROPERTY_PLUGINS_REPO_CREDENTIALS}" "${PLUGINS_PROPERTIES_FILE}")

deleteItemFromRepo "${repoUrl}" "${repoCredentials}" "${UPDATE_PLUGINS_XML_FILE_NAME}"
for pluginName in ${PLUGINS_DIRS_NAMES[@]}; do
  deleteItemFromRepo "${repoUrl}" "${repoCredentials}" "${pluginName}.zip"
done

logMessage "Successfully finished repository cleanup"