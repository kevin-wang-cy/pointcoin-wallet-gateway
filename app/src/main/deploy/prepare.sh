#!/bin/sh

set -e

#
chmod 777 /tmp

# mkdir /var/log/walletrpc
mkdir -p /var/log/walletrpc
chmod 700 /var/log/walletrpc
chown -R pointcoin:pointcoin /var/log/walletrpc