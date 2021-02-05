#!/bin/bash
if [ -f /.dockerenv ]; then
   echo "You can not synchonize the components inside a docker container"
else
	DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
	pushd $DIR >/dev/null
	declare -a SrcArray=("src/main/java/eu/internetofus/common" "src/main/resources/eu/internetofus/common" "src/test/java/eu/internetofus/common" "src/test/resources/eu/internetofus/common")
	declare -a ComponentArray=("profile-manager" "task-manager" "interaction-protocol-engine")
	for src in "${SrcArray[@]}"; do
    	echo "--- $src  ---"
    	for component in "${ComponentArray[@]}"; do
        	echo "* $component"
        	mkdir -p $DIR/../$component/$src/
        	rsync -r --delete -avzp $src/ $DIR/../$component/$src/
    	done
	done
	popd >/dev/null
fi