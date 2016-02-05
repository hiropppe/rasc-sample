FROM centos:6.6

USER root

RUN yum -y update

# install dev tools
RUN yum install -y \
    vim \
    wget \
    curl \
    tar \
    bzip2 \
    tree \
    openssh-server \
    openssh-clients \
    gcc \
    gcc-c++ \
    make \
    git \
    zlib-devel \
    libyaml-devel \
    openssl-devel

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

# system python
RUN curl -s https://www.python.org/ftp/python/2.7.10/Python-2.7.10.tgz | tar -xz -C . \
 && cd Python-2.7.10/ \
 && ./configure --with-threads --enable-shared --prefix=/usr/local \
 && make \
 && make altinstall \
 && ln -s /usr/local/lib/libpython2.7.so.1.0 /lib64/ \
 && echo "/usr/local/lib" >> /etc/ld.so.conf \
 && ldconfig \
 && ln -s /usr/local/bin/python2.7 /usr/local/bin/python \
 && ln -s /usr/local/bin/python2.7 /usr/local/bin/python2 \
 && curl -kL https://bootstrap.pypa.io/get-pip.py | python \
 && pip install PyYAML==3.11 \
 && pip install functools32==3.2.3.post2 \
 && pip install jctconv==0.1

# MeCab
RUN curl -s https://mecab.googlecode.com/files/mecab-0.996.tar.gz | tar -xz -C . \
 && cd mecab-0.996 \
 && ./configure \
 && make \
 && make check \
 && make install \
 && ldconfig

# MeCab-IPAdic
RUN curl -s https://mecab.googlecode.com/files/mecab-ipadic-2.7.0-20070801.tar.gz | tar -xz -C . \
 && cd ../mecab-ipadic-2.7.0-20070801 \
 && ./configure --with-charset=utf8 \
 && make \
 && make install

# JUMAN
RUN wget -O juman-7.01.tar.bz2 'http://nlp.ist.i.kyoto-u.ac.jp/DLcounter/lime.cgi?down=http://nlp.ist.i.kyoto-u.ac.jp/nl-resource/juman/juman-7.01.tar.bz2&name=juman-7.01.tar.bz2' \
 && tar jxf juman-7.01.tar.bz2 \
 && cd juman-7.01 \
 && ./configure --prefix=/usr/local \
 && make \
 && make install \
 && ldconfig \
 && cd .. \
 && rm -rf juman*

# KNP
RUN wget -O knp-4.16.tar.bz2 'http://nlp.ist.i.kyoto-u.ac.jp/DLcounter/lime.cgi?down=http://nlp.ist.i.kyoto-u.ac.jp/nl-resource/knp/knp-4.16.tar.bz2&name=knp-4.16.tar.bz2' \
 && tar jxf knp-4.16.tar.bz2 \
 && cd knp-4.16 \
 && ./configure --prefix=/usr/local --with-juman-prefix=/usr/local/ \
 && make \ 
 && make install \
 && ldconfig \
 && cd .. \
 && rm -rf knp*

# CaboCha
RUN curl -s https://cabocha.googlecode.com/files/cabocha-0.68.tar.bz2 | tar -xj -C . \
 && cd cabocha-0.68 \
 && ./configure \
 && make \
 && make check \
 && make install \
 && ldconfig

COPY run_knp /usr/local/bin/
RUN chown root:root /usr/local/bin/run_knp && chmod 700 /usr/local/bin/run_knp

RUN mkdir ~/.ssh

COPY bootstrap.sh /etc/bootstrap.sh
RUN chown root:root /etc/bootstrap.sh && chmod 700 /etc/bootstrap.sh
ENV BOOTSTRAP /etc/bootstrap.sh

CMD ["/etc/bootstrap.sh", "-d"]
