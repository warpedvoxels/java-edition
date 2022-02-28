#!/bin/bash

DIRNAME=$(dirname "$(realpath "$0")")
cd "$DIRNAME/../run"

if [ ! -f ./start ]; then
  echo "#!/bin/bash" >> start
  echo "java -Xmx1G -jar purpur.jar nogui" >> start
  chmod +x start
fi
if [ ! -f ./eula.txt ]; then
    echo "eula=true" > eula.txt
fi
if [ -f "./purpur.jar" ]; then
  if [ "$1" == "-f"]; then
    rm -f purpur.jar
  else
    echo "Could not overwrite purpur.jar. Use -f to force overwrite."
    exit -1
  fi
fi
curl https://api.purpurmc.org/v2/purpur/1.18.1/latest/download -o ./purpur.jar
