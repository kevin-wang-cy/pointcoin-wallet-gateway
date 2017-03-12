# pointcoin-wallet-api image, should refer to  pointcoin-wallet-api-<version>
# For example FROM kevinwangcy/upchain:pointcoin-wallet-api-latest
# By default the wallet itself is not secured, you can secured it through encryptwallet rpc command.
# And the rpc listens on 22888 and only grants access to pointcoinrpc with a password of 123456.
# Wallet data is kept under /wallet/$WALLET_ALIAS by default, it's recommanded to mount to /wallet from
# a data volue which contains all wallets' data.
# Usage:
# docker run --name wallet-default -p 8332:22888 -e WALLET_CONF_OPTS="-rpcallowip=192.168.0.2 -rpcallowip=192.168.0.7" kevinwangcy/upchain:pointcoin-wallet-pointcoind
# docker volume create wallet-data-volume
# docker run --name wallet-default -p 8332:22888 -e WALLET_ALIAS=default -v wallet-data-volume:/wallet kevinwangcy/upchain:pointcoin-wallet-pointcoind
# docker run --name wallet-default -p 8332:22888 -e WALLET_ALIAS=default -v ~/wallet:/wallet kevinwangcy/upchain:pointcoin-wallet-pointcoind

FROM kevinwangcy/upchain:pointcoin-wallet-pointcoind-latest
MAINTAINER Kevin C.Y. Wang <kevin.wang.cy@gmail.com>

# Install pointcoin-wallet-api
ADD . /tmp/docker_build
RUN chmod +x /tmp/docker_build/prepare.sh && \
    chmod +x /tmp/docker_build/pointcoin-wallet-api.jar && \
    chmod +x /tmp/docker_build/pointcoin-wallet-api.runit.sh && \
    chmod +x /tmp/docker_build/encryptor.sh && \
    sh /tmp/docker_build/prepare.sh && \
    mkdir -p /opt/pointcoin/wallet-api && \
    cp /tmp/docker_build/pointcoin-wallet-api.jar /opt/pointcoin/wallet-api/pointcoin-wallet-api.jar && \
    cp /tmp/docker_build/pointcoin-wallet-api.conf /opt/pointcoin/wallet-api/pointcoin-wallet-api.conf && \
    cp /tmp/docker_build/encryptor.sh /opt/pointcoin/wallet-api/encryptor.sh && \
    cp -R /tmp/docker_build/libs /opt/pointcoin/wallet-api && \
    ls -alF /opt/pointcoin/wallet-api/ && \
    mkdir /etc/service/pointcoin-wallet-api && \
    cp /tmp/docker_build/pointcoin-wallet-api.runit.sh /etc/service/pointcoin-wallet-api/run && \
    rm -rf /tmp/* /var/tmp/*

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
