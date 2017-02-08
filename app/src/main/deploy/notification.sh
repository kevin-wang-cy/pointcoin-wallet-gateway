#!/usr/bin/env bash

curl --user admin:123456 -i -X POST -H "Content-Type:application/json" -H "Accept:application/json" -d "{\"txid\":\"%1\"}" http://192.168.56.101:8080/api/mortgageaccounts/transactions  >> %1-%DATE:~10,4%%DATE:~4,2%%DATE:~7,2%%time:~0,2%%time:~3,2%%time:~6,2%.txt
