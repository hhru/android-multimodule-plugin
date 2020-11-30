#!/bin/bash

source infra/constants.sh
source infra/utils.sh


logMessage "Push plugins to repository..."

readonly repoUrl=$(prop "${PROPERTY_PLUGINS_REPO_URL}" "${PLUGINS_PROPERTIES_FILE}")
readonly repoCredentials=$(prop "${PROPERTY_PLUGINS_REPO_CREDENTIALS}" "${PLUGINS_PROPERTIES_FILE}")

for filename in ./build/plugins/*; do
  putItemIntoRepo "${repoUrl}" "${repoCredentials}" "${filename}"
done

logMessage "Successfully pushed every plugin distribution to repository"