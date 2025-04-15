FROM debian:latest

RUN apt update
RUN apt install -y wget gnupg2 lsb-release

RUN sh -c 'echo "deb https://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list'

RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -

RUN apt update

RUN apt -y install postgresql

VOLUME /var/lib/postgresql/data

EXPOSE 5432

# Install newest Java
########################################################################################################################

WORKDIR /root/downloads

RUN wget "https://download.oracle.com/java/23/latest/jdk-23_linux-x64_bin.deb"

RUN apt -y install ./jdk-23_linux-x64_bin.deb

# Install SSH
#########################################################################################################################

RUN apt install -y openssh-client openssh-server

COPY .ssh /root/.ssh/
RUN chmod 600 /root/.ssh/id_rsa && echo "StrictHostKeyChecking no" > /root/.ssh/config
RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN ssh-keyscan github.com >> /root/.ssh/known_hosts

RUN mkdir /var/run/sshd
RUN chmod 755 /var/run/sshd
RUN chown root:root /var/run/sshd

# Install Wildfly
########################################################################################################################


#RUN apt install -y systemctl
#RUN apt install -y systemd-sysv

WORKDIR /root/downloads

# == Create a wildfly user

RUN  groupadd -r wildfly
RUN  useradd -r -g wildfly -d /opt/wildfly -s /sbin/nologin wildfly

# == Install WildFly

RUN wget "https://github.com/wildfly/wildfly/releases/download/34.0.1.Final/wildfly-preview-34.0.1.Final.tar.gz"

RUN mkdir /opt/wildfly

RUN tar -xzf wildfly-preview-34.0.1.Final.tar.gz

RUN mv /root/downloads/wildfly-preview-34.0.1.Final/* /opt/wildfly

RUN chown -R wildfly:wildfly /opt/wildfly

# == Configure systemd

#RUN mkdir /etc/wildfly
#RUN cp /opt/wildfly/docs/contrib/scripts/systemd/wildfly.conf /etc/wildfly/
#RUN cp /opt/wildfly/docs/contrib/scripts/systemd/wildfly.service /etc/systemd/system/
#RUN cp /opt/wildfly/docs/contrib/scripts/systemd/launch.sh /opt/wildfly/bin/
#RUN chmod +x /opt/wildfly/bin/launch.sh

COPY scripts/wildfly-init-debian.sh /etc/init.d/wildfly
COPY scripts/wildfly.conf /etc/default/wildfly
RUN update-rc.d wildfly defaults


COPY modules/ /opt/wildfly/modules/
COPY cli/ /opt/wildfly/bin/

WORKDIR /opt/wildfly/bin

EXPOSE 8080
EXPOSE 8787
EXPOSE 9990

# Clone Git Repositories
######################################################################################################################

RUN apt install -y mc maven git sudo

WORKDIR /home/hairstyle

RUN git clone git@github.com:anjunar/hairstyle-parent.git

WORKDIR /home/hairstyle/hairstyle-parent/

RUN mvn clean install

WORKDIR /opt/wildfly/bin

COPY startup/ /

CMD ["/startup.sh"]

# /usr/local/var/postgres/postgresql.conf -> listen_addresses = '*'
# /usr/local/var/postgres/pg_hba.conf -> host all all 172.17.0.1/32 trust

# docker images
# docker run -p 8080:8080 -p 8787:8787 -p 9990:9990 -p 5432:5432 90957c08ae2e
