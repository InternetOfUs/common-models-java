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
		if [ $? -eq 0 ]; then
			popd >/dev/null
			popd >/dev/null
			exit 1
		else
			popd >/dev/null
		fi
	done
	popd >/dev/null
fi