# pointcoin-wallet-api image, should refer to  pointcoin-wallet-api-<version>
# For example FROM kevinwangcy/upchain:pointcoin-wallet-api-latest
# By default the wallet itself is not secured, you can secured it through encryptwallet rpc command.
# And the rpc listens on 22888 and only grants access to pointcoinrpc with a password of 123456.
# Wallet data is kept under /wallet/$WALLET_ALIAS by default, it's recommanded to mount to /wallet from
# a data volume which contains all wallets' data.
#
# Usage:
#
# docker run -d -p 3306:3306 \
#       --name pointcoindb-mysql \
#       -e MYSQL_ROOT_PASSWORD=Rdisfun4Root \
#       -e MYSQL_DATABASE=pointcoindb \
#       -e MYSQL_USER=pointcoinapiremote \
#       -e MYSQL_PASSWORD=Rdisfun4pointcoinapiremote \
#       mysql:latest
#
# docker run --name wallet-default -p 8080:8080 -p 22888:22888
#       -e WALLET_ALIAS=default \
#       -v ~/wallet:/wallet \
#       kevinwangcy/upchain:pointcoin-wallet-api-latest
#
# docker volume create wallet-data-volume
#
# docker run --name wallet-default -p 8080:8080 \
#       -e WALLET_ALIAS=default \
#       -v wallet-data-volume:/wallet \
#       kevinwangcy/upchain:pointcoin-wallet-api-latest
#
# docker run --name wallet-default --link pointcoindb-mysql:mysql -p 8080:8080
#       -e POINTCOIN_SECURITY_ALLOWIP="10.0.0.0/8" \
#       -e WALLET_ALIAS=default \
#       -v wallet-data-volume:/wallet \
#       kevinwangcy/upchain:pointcoin-wallet-api-latest
#
# docker run --rm kevinwangcy/upchain:pointcoin-wallet-api-latest \
#       encrypt pasword="<jasypt password>" input="<your password>"
#
# docker run --name wallet-default -p 8080:8080 \
#       -e JASYPT_ENCRYPTOR_PASSWORD="<jasypt password>"  \
#
#       -e POINTCOIN_MQSQL_HOST="<mysql db ipaddress>" "\
#       -e POINTCOIN_MQSQL_PORT=<mysql port> \
#       -e POINTCOIN_MQSQL_DATABASE=<pointcoindb> \
#       -e POINTCOIN_MQSQL_USER=<pointcoinapiremote> \
#       -e POINTCOIN_MQSQL_PASSWORD=ENC(jasypt encrypted password)  \
#
#       -e POINTCOIN_SECURITY_ALLOWIP="<member system ip;wallet-frontend ip>" \
#
#       -e POINTCOIN_TASK_DR_PUSH_USER=<member system service account> \
#       -e POINTCOIN_TASK_DR_PUSH_PASSWORD=ENC(jasypt encrytped password) \
#
#       -e POINTCOIN_SECURITY_USER_PASSWORD=ENC(jasypt encrytped password for user) \
#       -e POINTCOIN_SECURITY_ADMIN_PASSWORD=ENC(jasypt encrytped password for admin) \
#
#       -e WALLET_ALIAS=default \
#       -v wallet-data-volume:/wallet \
#       kevinwangcy/upchain:pointcoin-wallet-api-latest
#
FROM kevinwangcy/upchain:pointcoin-wallet-pointcoind-latest
MAINTAINER Kevin C.Y. Wang <kevin.wang.cy@gmail.com>

# Jasypt Password Settings
ENV JASYPT_ENCRYPTOR_PASSWORD=25164dbe226e1d26c109b64c7e6d14e8

# Member System EndPoints Settings
ENV POINTCOIN_TASK_DR_PUSH_URL=http://127.0.0.1:8080/api/echo
ENV POINTCOIN_TASK_DR_PUSH_CRON="0 0/5 * 1/1 * ?"
ENV POINTCOIN_TASK_DR_PUSH_USER=
ENV POINTCOIN_TASK_DR_PUSH_PASSWORD=

# LOG_PATH
ENV LOG_PATH=/var/log/walletrpc

# Pointcoin Security Settings
ENV POINTCOIN_SECURITY_ALLOWIP=127.0.0.1
ENV POINTCOIN_SECURITY_USER_PASSWORD=ENC(kYCyrt/wFOl6iypL4PS6Hg==)
ENV POINTCOIN_SECURITY_ADMIN_PASSWORD=ENC(9wsc+eE6tDhmvBpBvSwaSg==)

# Install pointcoin-wallet-api
ARG WALLETAPIPKG_NEXUS=139.196.120.42
ARG WALLETAPIPKG_VERSION=0.14
ARG WALLETAPIPKG_FILE=pointcoin-wallet-gateway-app-pkg-$WALLETAPIPKG_VERSION.tar
ARG WALLETAPIPKG_URL=http://$WALLETAPIPKG_NEXUS:8081/repository/maven-releases/com/upbchain/pointcoin/pointcoin-wallet-gateway-app-pkg/$WALLETAPIPKG_VERSION/$WALLETAPIPKG_FILE

RUN curl -L -o /tmp/$WALLETAPIPKG_FILE $WALLETAPIPKG_URL && \
    mkdir -p /tmp/docker_build && \
    tar -C /tmp/docker_build -xvf /tmp/$WALLETAPIPKG_FILE && \
    chmod +x /tmp/docker_build/prepare.sh && \
    chmod +x /tmp/docker_build/pointcoin-wallet-api.jar && \
    chmod +x /tmp/docker_build/pointcoin-wallet-api.runit.sh && \
    sh /tmp/docker_build/prepare.sh && \
    mkdir -p /opt/pointcoin/wallet-api && \
    cp /tmp/docker_build/pointcoin-wallet-api.jar /opt/pointcoin/wallet-api/pointcoin-wallet-api.jar && \
    cp /tmp/docker_build/pointcoin-wallet-api.conf /opt/pointcoin/wallet-api/pointcoin-wallet-api.conf && \
    ls -alF /opt/pointcoin/wallet-api/ && \
    mkdir /etc/service/pointcoin-wallet-api && \
    cp /tmp/docker_build/pointcoin-wallet-api.runit.sh /etc/service/pointcoin-wallet-api/run && \
    rm -rf /tmp/* /var/tmp/*

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
