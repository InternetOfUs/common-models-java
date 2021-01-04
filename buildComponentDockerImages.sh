#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR >/dev/null
declare -a ComponentArray=("profile-manager" "task-manager" "interaction-protocol-engine")
for component in "${ComponentArray[@]}"; do
  echo "* $component"
  pushd $DIR/../$component > /dev/null
  ./buildDockerImage.sh
  popd >/dev/null
done
popd >/dev/null