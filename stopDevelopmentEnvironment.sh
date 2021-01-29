#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR >/dev/null
docker stop wenet_common_dev
docker rm wenet_common_dev
popd >/dev/null