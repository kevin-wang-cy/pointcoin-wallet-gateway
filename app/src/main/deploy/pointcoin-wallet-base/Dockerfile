# pointcoin-wallet-base image, should refer to  pointcoin-wallet-base-<version>
# For example FROM kevinwangcy/upchain:pointcoin-wallet-base-latest

FROM phusion/baseimage:0.9.19
MAINTAINER Kevin C.Y. Wang <kevin.wang.cy@gmail.com>

# Upgrade OS, Install OpenJDK 8 & bzip2
RUN add-apt-repository ppa:openjdk-r/ppa -y && apt-get update -y && \
    apt-get install -y --no-install-recommends openjdk-8-jdk bzip2 libevent-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
