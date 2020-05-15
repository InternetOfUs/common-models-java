#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
pushd $DIR
declare -a SrcArray=("src/main/java/eu/internetofus/common" "src/main/resources/eu/internetofus/common" "src/test/java/eu/internetofus/common" "src/test/resources/eu/internetofus/common")
declare -a ComponentArray=("wenet-interaction-protocol-engine" "wenet-profile-manager" "wenet-task-manager")
for src in "${SrcArray[@]}"; do
    echo "--- $src  ---"
    for component in "${ComponentArray[@]}"; do
        echo "* $component"
        mkdir -p $DIR/../$component/$src/
        rsync -r --delete -avzp $src/ $DIR/../$component/$src/
    done
done
