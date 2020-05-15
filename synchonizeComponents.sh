#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR
declare -a SrcArray=("src/main/java/eu/internetofus/common" "src/main/resources/eu/internetofus/common" "src/test/java/eu/internetofus/common" "src/test/resources/eu/internetofus/common")
declare -a ComponentArray=("wenet-interaction-protocol-engine" "wenet-profile-manager" "wenet-task-manager")
for src in "${SrcArray[@]}"; do
    for component in "${ComponentArray[@]}"; do
	mkdir -p $DIR/../$component/$src/
        rsync -r --delete --ignore-existing -avzp $src/ $DIR/../$component/$src/
    done
done
