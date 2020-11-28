#!/bin/bash

#source infra/constants.sh

function logMessage() {
    echo "${LOG_TAG} ${1}"
}

# Fetch property value by key ($1) from property file ($2)
function prop() {
    grep "${1}" ${2}|cut -d'=' -f2
}

# Send 'DELETE' request to server ${1} for removing item ${3} with ${2} credentials
function deleteItemFromRepo() {
  innerRepoUrl=${1}
  innerRepoCredentials=${2}
  innerItemName=${3}


  logMessage "DELETE '${innerItemName}' from repository" >&2
  curl \
    -X DELETE \
    -u "${innerRepoCredentials}" \
    "${innerRepoUrl}/${innerItemName}"
  logMessage "Done DELETE '${innerItemName}' from repository" >&2
}

# Send 'PUT' request to server ${1} for updating item from path ${3} with ${2} credentials
function putItemIntoRepo() {
  innerRepoUrl=${1}
  innerRepoCredentials=${2}
  innerItemPath=${3}


  logMessage "PUT '${innerItemPath}' into repository" >&2
  curl \
    -X PUT \
    -T ${innerItemPath} \
    -u ${innerRepoCredentials} \
    ${innerRepoUrl}/ \
    --http1.1
  logMessage "Done PUT '${innerItemPath}' into repository" >&2
}