# pointcoin-wallet-pointcoind image, should refer to  pointcoin-wallet-pointcoind-<version>
# For example FROM kevinwangcy/upchain:pointcoin-wallet-pointcoind-latest
# By default the wallet itself is not secured, you can secured it through encryptwallet rpc command.
# And the rpc listens on 22888 and only grants access to pointcoinrpc with a password of 123456. 
# Wallet data is kept under /wallet/$WALLET_ALIAS by default, it's recommanded to mount to /wallet from 
# a data volue which contains all wallets' data.
# Usage:
# docker run --name wallet-default -p 8332:22888 -e WALLET_CONF_OPTS="rpcallowip=192.168.0.2 rpcallowip=192.168.0.7" kevinwangcy/upchain:pointcoin-wallet-pointcoind
# docker volume create wallet-data-volume
# docker run --name wallet-default -p 8332:22888 -e WALLET_ALIAS=default -v wallet-data-volume:/wallet kevinwangcy/upchain:pointcoin-wallet-pointcoind


FROM kevinwangcy/upchain:pointcoin-wallet-base-latest
MAINTAINER Kevin C.Y. Wang <kevin.wang.cy@gmail.com>

# wallet alias
ENV WALLET_ALIAS=default
ENV WALLET_CONF=/home/pointcoin/.pointcoind/pointcoin.conf
ENV WALLET_CONF_OPTS=

# Install Pointcoind
ARG POINTCOIND_FILE=pointcoind-1.0.2.0-xenial-amd64.tar
ARG POINTCOIND_URL=https://raw.githubusercontent.com/kevin-wang-cy/pointcoin-resource/master/poincoind/$POINTCOIND_FILE

RUN curl -L -o /tmp/$POINTCOIND_FILE $POINTCOIND_URL && \
    tar -C /tmp -xvf /tmp/$POINTCOIND_FILE && \
    chmod +x /tmp/pointcoind && \
    cp /tmp/pointcoind /usr/local/bin/pointcoind && \
    rm -rf /tmp/* /var/tmp/*

ADD . /tmp/docker_build
RUN chmod +x /tmp/docker_build/prepare.sh && \
    chmod +x /tmp/docker_build/pointcoind.runit.sh && \
    sh tmp/docker_build/prepare.sh && \
    ls -alF /home/pointcoin/ && \
    cp /tmp/docker_build/pointcoin.conf /home/pointcoin/.pointcoind/ && \
    mkdir /etc/service/pointcoind && \
    cp /tmp/docker_build/pointcoind.runit.sh /etc/service/pointcoind/run && \
    rm -rf /tmp/* /var/tmp/*

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
