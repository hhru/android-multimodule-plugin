#!/bin/bash

bash infra/scripts/build_all_plugins.sh
bash infra/scripts/collect_update_plugins_xml.sh
bash infra/scripts/cleanup_plugins_repository.sh
bash infra/scripts/push_plugins_to_repository.sh