#!/bin/sh
set -e

exec chpst -u pointcoin /opt/pointcoin/wallet-api/pointcoin-wallet-api.jar
