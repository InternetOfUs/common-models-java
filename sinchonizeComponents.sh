#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR
declare -a SrcArray=("src/main/java/eu/internetofus/common" "src/test/java/eu/internetofus/common")
declare -a ComponentArray=("wenet-interaction-protocol-engine" "wenet-profile-manager" "wenet-task-manager")
for src in "${SrcArray[@]}"; do
    for component in "${ComponentArray[@]}"; do
        rsync -avzp $src $DIR/../$component/$src
    done
done
