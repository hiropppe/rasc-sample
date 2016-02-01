## RaSCメモ

作業中。。。

環境
- Fabric 1.10.2
- Ant 1.9.6
- Docker 1.8.2
- Docker Compose 1.3.1

### MeCabの分散実行
呼ぶスクリプトを変えれば、JUMANも、JUMAN+KNPも、MeCab+J.DepP同じ

コンテナの起動
```
$ docker-compose up -d
$ ./update_docker_hosts.sh
```
キーの配布
```
ssh-keygen -t rsa -P '' -f ~/.ssh/docker_rsa -C "hiropppe@github.com" 
cat ~/.ssh/docker_rsa.pub >> ~/.ssh/authorized_keys
cat /etc/docker-container-hosts | grep 'containers.dev' | awk '{print$1}' | while read ip; do scp ~/.ssh/authorized_keys root@${ip}:/root/.ssh/; done
```

モジュール配備
```
$ cd parallel-parser/deploy/
$ fab config:'deploy.properties' deployAllservers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' deployAllworkers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' deployproxy -u root -i ~/.ssh/docker_rsa
```

サーバ起動
```
$ fab config:'deploy.properties' startAllservers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' startAllworkers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' startproxy -u root -i ~/.ssh/docker_rsa
```

サーバ停止
```
$ fab config:'deploy.properties' stopproxy -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' stopAllworkers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' stopAllservers -u root -i ~/.ssh/docker_rsa
```
