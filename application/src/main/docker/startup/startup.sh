#!/bin/bash

rm /var/run/wildfly/wildfly.pid

service postgresql start
service ssh start
service wildfly start

db_flag_file="/var/run/database_created"
jboss_flag_file="/var/run/jboss_altered"

if [ ! -f "$db_flag_file" ]; then
  sudo -u postgres createdb hairstyle
  sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'postgres'";
  touch "$db_flag_file"
fi

if [ ! -f "$jboss_flag_file" ]; then
  cd /opt/wildfly/bin
  ./jboss-cli.sh --connect --file=configure-elytron.cli
  ./jboss-cli.sh --connect --file=configure-database.cli
  ./jboss-cli.sh --connect --file=configure-mail.cli
  ./jboss-cli.sh --connect --file=configure-undertow.cli
  ./add-user.sh -a -u admin -p admin -g admin
  service wildfly restart
  cp /home/hairstyle/hairstyle-parent/application/target/*.war /opt/wildfly/standalone/deployments
  touch "$jboss_flag_file"
fi

tail -f /opt/wildfly/standalone/log/server.log