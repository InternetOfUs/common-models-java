#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not build docker component images inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd "$DIR" >/dev/null
	declare -a ComponentArray=("profile-manager" "task-manager" "interaction-protocol-engine" "profile-diversity-manager")
	for component in "${ComponentArray[@]}"; do
		echo "* $component"
		pushd "$DIR/../$component" > /dev/null
		./buildDockerImage.sh $@
		popd >/dev/null
	done
	popd >/dev/null
fi