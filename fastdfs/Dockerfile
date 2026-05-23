FROM docker.1ms.run/ubuntu:noble

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

RUN rm  /etc/apt/sources.list.d/*
COPY sources.list /etc/apt/sources.list.d/
RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl gpg procps netcat-openbsd wget gcc git build-essential zlib1g zlib1g-dev libssl-dev tar libpcre3 libpcre3-dev

WORKDIR /usr/local/src
RUN git clone https://gh-proxy.org/https://github.com/happyfish100/libfastcommon.git --depth 1 \
    && cd libfastcommon/ \
    && ./make.sh && ./make.sh install \
    && cd ../ 
RUN git clone https://gh-proxy.org/https://github.com/happyfish100/libserverframe.git --depth 1 \
    && cd libserverframe/ \
    && ./make.sh && ./make.sh install \
    && cd ../ 
RUN git clone https://gh-proxy.org/https://github.com/happyfish100/fastdfs.git --depth 1 \
    && cd fastdfs/ \
    && ./make.sh && ./make.sh install \
    && cp /usr/local/src/fastdfs/conf/http.conf /etc/fdfs/ \
    && cp /usr/local/src/fastdfs/conf/mime.types /etc/fdfs/ \
    && cp /usr/local/src/fastdfs/conf/tracker.conf /etc/fdfs/ \
    && cp /usr/local/src/fastdfs/conf/storage.conf /etc/fdfs/ \
    && cp /usr/local/src/fastdfs/conf/client.conf /etc/fdfs/ \
    && cd ../ 
RUN git clone https://gh-proxy.org/https://github.com/happyfish100/fastdfs-nginx-module.git --depth 1 \
    && cp /usr/local/src/fastdfs-nginx-module/src/mod_fastdfs.conf /etc/fdfs \
    && wget http://nginx.org/download/nginx-1.15.4.tar.gz \
    && tar -zxvf nginx-1.15.4.tar.gz \
    && cd nginx-1.15.4/ \
    && ./configure --add-module=/usr/local/src/fastdfs-nginx-module/src/ \
    && make && make install


COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 22122 23000 8888

ENTRYPOINT ["/entrypoint.sh"]
