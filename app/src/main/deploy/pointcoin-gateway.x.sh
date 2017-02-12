#!/bin/sh
set -e


POINTCOIND_FILE=pointcoind-1.0.1.0-xenial-amd64.tar
POINTCOIND_URL=https://raw.githubusercontent.com/kevin-wang-cy/pointcoin-resource/master/poincoind/$POINTCOIND_FILE
RUNDIR=/var/run/pointcoind
PIDFILE=$RUNDIR/poincoind.pid




curl -L -o /tmp/$POINTCOIND_FILE $POINTCOIND_URL && \
    tar -C $dir -xzf $file
  chmod +x /tmp/docker-machine &&
  sudo cp /tmp/docker-machine /usr/local/bin/docker-machine


  file=tar123.tar.gz
  dir=/myunzip/${file%.tar.gz}
  mkdir -p $dir
  tar -C $dir -xzf $file


mkdir -p $RUNDIR
touch $PIDFILE
chown pointcoin:pointcoin $RUNDIR $PIDFILE
chmod 755 $RUNDIR

mkdir /etc/service/pointcoind
cp /pointcoin_build/pointcoind.runit.sh /etc/service/pointcoind/run

touch /etc/service/pointcoind/down