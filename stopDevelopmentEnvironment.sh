#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not stop the development environment inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd "$DIR" >/dev/null
	if [ "$(docker container ls |grep wenet_common_dev |wc -l)" -gt "0" ]
	then
		docker stop wenet_common_dev
	fi
	popd >/dev/null
fi