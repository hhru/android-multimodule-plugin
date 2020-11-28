#!/bin/bash

source infra/constants.sh
source infra/utils.sh


logMessage "Build all plugins..."

for pluginName in ${PLUGINS_DIRS_NAMES[@]}; do
  bash gradlew :${pluginName}:buildPlugin
done

logMessage "Successfully build all plugins"