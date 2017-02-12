#!/bin/sh
set -e

WALLET_DATADIR=/wallet/$WALLET_ALIAS

if [ ! -d "$WALLET_DATADIR" ]; then
    mkdir -p "$WALLET_DATADIR"
    echo "$WALLET_DTADIR created."
fi

chown -R pointcoin:pointcoin $WALLET_DATADIR

exec chpst -u pointcoin /usr/local/bin/pointcoind -datadir=$WALLET_DATADIR -conf=$WALLET_CONF $WALLET_CONF_OPTS

