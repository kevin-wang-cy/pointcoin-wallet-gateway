#!/bin/bash
set -e

inputstring="password=POINTCOIN[$JASYPT_ENCRYPTOR_PASSWORD] $@"

if [[ "$@" == *"password="* ]]; then
    inputstring="$@"
fi

sh /opt/pointcoin/jasypt/bin/decrypt.sh $inputstring
