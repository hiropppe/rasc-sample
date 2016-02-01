FROM centos:6.6

USER root

RUN yum -y update

# install dev tools
RUN yum install -y \
    vim \
    wget \
    curl \
    tar \
    tree \
    openssh-server \
    openssh-clients \
    gcc \
    gcc-c++ \
    make

# JST
RUN cp /usr/share/zoneinfo/Japan /etc/localtime \
 && echo "ZONE=\"Asia/Tokyo\"" > /etc/sysconfig/clock

# allow empty password root ssh
RUN echo "Port 22" >> /etc/ssh/sshd_config \
 && sed -ri 's/^#PermitEmptyPasswords no/PermitEmptyPasswords yes/' /etc/ssh/sshd_config \
 && sed -ri 's/^#PermitRootLogin yes/PermitRootLogin yes/' /etc/ssh/sshd_config \
 && sed -ri 's/^UsePAM yes/UsePAM no/' /etc/ssh/sshd_config \
 && passwd -d root

# java
RUN curl -LO 'http://download.oracle.com/otn-pub/java/jdk/8u72-b15/jdk-8u72-linux-x64.rpm' -H 'Cookie: oraclelicense=accept-securebackup-cookie' \
 && rpm -i jdk-8u72-linux-x64.rpm \
 && rm jdk-8u72-linux-x64.rpm

ENV JAVA_HOME=/usr/java/default 

ENV PATH=$PATH:~/bin:$JAVA_HOME/bin

RUN echo '# JDK' >> ~/.bash_profile \
 && echo "export JAVA_HOME=$JAVA_HOME" >> ~/.bash_profile \
 && echo 'export PATH=$PATH:$JAVA_HOME/bin' >> ~/.bash_profile \
 && echo '' >> ~/.bash_profile

# MeCab
RUN curl -s https://mecab.googlecode.com/files/mecab-0.996.tar.gz | tar -xz -C . \
 && cd mecab-0.996 \
 && ./configure \
 && make \
 && make check \
 && make install

RUN curl -s https://mecab.googlecode.com/files/mecab-ipadic-2.7.0-20070801.tar.gz | tar -xz -C . \
 && cd ../mecab-ipadic-2.7.0-20070801 \
 && ./configure --with-charset=utf8 \
 && make \
 && make install

ADD bootstrap.sh /etc/bootstrap.sh
RUN chown root:root /etc/bootstrap.sh && chmod 700 /etc/bootstrap.sh
ENV BOOTSTRAP /etc/bootstrap.sh

RUN mkdir ~/.ssh

CMD ["/etc/bootstrap.sh", "-d"]
