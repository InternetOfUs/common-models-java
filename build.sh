#!/bin/bash
ARGS="clean install"
if [[ $# -ne 0 ]] ; then
	ARGS="$@"
fi
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR >/dev/null
docker run -it --rm --name common-build -v "$HOME/.m2":/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven maven:3.6.3-jdk-11-slim mvn $ARGS
popd >/dev/null
