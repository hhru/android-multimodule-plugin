#!/bin/bash

readonly LOG_TAG="[hh-plugins]"

readonly PLUGINS_PROPERTIES_FILE="plugins.properties"
readonly PROPERTY_PLUGINS_REPO_URL="plugins.customRepositoryUrl.url"
readonly PROPERTY_PLUGINS_REPO_CREDENTIALS="plugins.customRepositoryUrl.credentials"

readonly UPDATE_PLUGINS_XML_FILE_NAME="updatePlugins.xml"

readonly PLUGINS_DIRS_NAMES=(
  "hh-carnival"
  "hh-garcon"
  "hh-geminio"
)