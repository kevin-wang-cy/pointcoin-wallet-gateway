#!/bin/sh
set -e

RUNDIR=/var/run/pointcoin-gateway
PIDFILE=$RUNDIR/pointcoin-gateway.pid

mkdir -p $RUNDIR
touch $PIDFILE
chown pointcoin:pointcoin $RUNDIR $PIDFILE
chmod 755 $RUNDIR

exec chpst -u pointcoin /usr/bin/pointcoind -datadir=/data/wallet-$WALLET_ALIAS