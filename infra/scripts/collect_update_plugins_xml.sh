#!/bin/bash

source infra/constants.sh
source infra/utils.sh

readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")

logMessage "Collect ${UPDATE_PLUGINS_XML_FILE_NAME} file..."

bash gradlew collectUpdatePluginsXmlTask --customRepositoryUrl="${repoUrl}"

logMessage "Successfully created ${UPDATE_PLUGINS_XML_FILE_NAME} at ./build/${UPDATE_PLUGINS_XML_FILE_NAME}"