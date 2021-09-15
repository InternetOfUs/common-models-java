#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not start the development environment inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd "$DIR" >/dev/null
	DOCKER_BUILDKIT=1 docker build -f src/dev/docker/Dockerfile -t internetofus/common:dev .
	docker run --rm --name wenet_common_dev -v /var/run/docker.sock:/var/run/docker.sock -v "${HOME}/.m2":/root/.m2  -v "${PWD}":/app -p 5000:5005 -it internetofus/common:dev /bin/bash
	./stopDevelopmentEnvironment.sh
	popd >/dev/null
fi