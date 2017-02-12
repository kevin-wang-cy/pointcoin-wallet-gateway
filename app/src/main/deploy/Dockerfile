FROM kevinwangcy/upchain:pointcoin-wallet-pointcoind-latest
MAINTAINER Kevin C.Y. Wang <kevin.wang.cy@gmail.com>

# Install Pointcoind
ENV XX

ADD . /tmp/docker_build

RUN /tmp/docker_build/prepare.sh && \
    cp /tmp/docker_build/pointcoin.conf /$HOME/pointcoin/.pointcoind/ && \
    mkdir /etc/service/pointcoind && \
    cp /tmp/docker_build/pointcoind.runit.sh /etc/service/pointcoind/run

# Clean up APT when done.
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]
# EXPOSE 80 443
