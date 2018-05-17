#!/bin/bash
#: Title : IFaceEngine.stop.sh -8082
#: Date : 2016-01-07
#: Author : "Knight.Zhou" <youngwelle@gmail.com>
#: Version : 1.0
#: Description : restart IFaceServer!
#: Options : None
#: Example : if fuser -n tcp 8082; then kill -9 $(lsof -i:8082 -t);else echo 'none of 8082'; fi
#: stop it
echo "Stopping..."
if fuser -n tcp $1; then kill -9 $(lsof -i:$1 -t);else echo 'none of $1'; fi