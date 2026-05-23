#!/usr/bin/env bash
IP=$(hostname -I | awk '{print $1}')

sed -ri "s|^tracker_server = .*|tracker_server=${IP}:22122|" /etc/fdfs/storage.conf
sed -ri "s|^tracker_server = .*|tracker_server=${IP}:22122|" /etc/fdfs/client.conf
sed -ri "s|^tracker_server=.*|tracker_server=${IP}:22122|" /etc/fdfs/mod_fastdfs.conf
sed -ri "s|^url_have_group_name = .*|url_have_group_name = true|" /etc/fdfs/mod_fastdfs.conf
sed -ri "s|^store_path0=.*|store_path0=/opt/fastdfs|" /etc/fdfs/mod_fastdfs.conf

NGINX_HOME=/usr/local/nginx
CONF_DIR=${NGINX_HOME}/conf
CONF_D_DIR=${CONF_DIR}/conf.d

mkdir -p "${CONF_D_DIR}"

cat > "${CONF_D_DIR}/fastdfs.conf" <<'EOF'
server {
    listen       8888;
    server_name  localhost;

    location ~ /group[0-9]/ {
        ngx_fastdfs_module;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   html;
    }
}
EOF

if ! grep -q 'conf.d/\*\.conf' "${CONF_DIR}/nginx.conf"; then
    sed -i '/http[[:space:]]*{/a\
    include       conf.d/*.conf;' "${CONF_DIR}/nginx.conf"
fi

${NGINX_HOME}/sbin/nginx -t


/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start
/usr/bin/fdfs_storaged /etc/fdfs/storage.conf start
${NGINX_HOME}/sbin/nginx

exec tail -F /opt/fastdfs/logs/trackerd.log /opt/fastdfs/logs/storaged.log
