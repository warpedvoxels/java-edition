#!/bin/bash

cd "${0%/*}"
cd ../run

if [ ! -f ./start ]; then
  echo "#!/bin/bash" >> start
  echo "java -Xmx1G -jar purpur.jar" >> start
  chmod +x start
fi
curl https://api.purpurmc.org/v2/purpur/1.18.1/latest/download -o ./purpur.jar