#!/bin/bash

source infra/constants.sh
source infra/utils.sh


logMessage "Cleanup plugins repository..."

readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")
readonly repoCredentials=$(prop "${PROPERTY_PLUGINS_REPO_CREDENTIALS}" "${PLUGINS_PROPERTIES_FILE}")

for filename in ./build/plugins/*; do
  deleteItemFromRepo "${repoUrl}" "${repoCredentials}" "$(basename "${filename}")"
done

logMessage "Successfully finished repository cleanup"