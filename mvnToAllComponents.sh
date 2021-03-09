#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not build docker component images inside a docker container"
else
	ARGS="clean install -DskipTests"
	if [ "$#" -ne 0 ]; then
		ARGS="$@"
	fi
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR >/dev/null
	declare -a ComponentArray=("common" "profile-manager" "task-manager" "interaction-protocol-engine")
	for component in "${ComponentArray[@]}"; do
		pushd $DIR/../$component > /dev/null
		docker run -v ${HOME}/.m2/repository:/root/.m2/repository -v ${PWD}:/app -it internetofus/$component:dev mvn $ARGS
		popd >/dev/null
	done
fi
