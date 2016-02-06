## RaSCメモ
RaSCを使って入力文書の解析を複数のノードに分散させるメモ

### 参考
チュートリアル3-1: 分散実行による負荷分散
<https://alaginrc.nict.go.jp/rasc/ja/rasc_parallel.html>

### 環境
- Fabric 1.10.2
- Ant 1.9.6
- Docker 1.8.2
- Docker Compose 1.3.1

### 解析器の設定
ParserResourceApiWrapperでStdIOCommandParallelArrayServiceを初期化
サンプルでは、juman | knp -tab を実行 

### 起動とテスト
Dockerコンテナの準備
```
# コンテナ起動
$ cd parallel-parser/docker/
$ docker-compose up -d
$ ./update_docker_hosts.sh

# モジュールの配備に使うキーの配布
$ ssh-keygen -t rsa -P '' -f ~/.ssh/docker_rsa -C "hiropppe@github.com" 
$ cat ~/.ssh/docker_rsa.pub >> ~/.ssh/authorized_keys
$ cat /etc/docker-container-hosts | grep 'containers.dev' | awk '{print$1}' | while read ip; do scp ~/.ssh/authorized_keys root@${ip}:/root/.ssh/; done
```

モジュール配備
```
$ cd parallel-parser/deployer/
$ sed s/PROXY/$(cat /etc/docker-container-hosts | grep 'proxy' | awk '{print substr($1, 10)}')/g deploy.properties.template | \
> sed s/SERVER/$(cat /etc/docker-container-hosts | grep 'server' | awk '{print substr($1, 10)}')/g | \
> sed s/WORKER1/$(cat /etc/docker-container-hosts | grep 'worker1' | awk '{print substr($1, 10)}')/g | \
> sed s/WORKER2/$(cat /etc/docker-container-hosts | grep 'worker2' | awk '{print substr($1, 10)}')/g > deploy.properties
$ fab config:'deploy.properties' deployAllservers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' deployAllworkers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' deployproxy -u root -i ~/.ssh/docker_rsa
```

### サーバ起動
```
$ fab config:'deploy.properties' startAllservers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' startAllworkers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' startproxy -u root -i ~/.ssh/docker_rsa
```

### テストリクエスト
```
In [1]: import requests, json

In [2]: url = 'http://192.168.1.10:8080/jp.go.nict.ial.webapps.wisdom.proxyservice/jsonServices/ParserServer'

In [3]: headers = {'content-type': 'application/json-rpc'}

In [4]: sents = [u'クロールで泳いでいる少女を見た', u'望遠鏡で泳いでいる少女を見た']

In [5]: params = {
   ...:   'method': 'process',
   ...:   'params': [[{'seq': i, 'sent': s} for i, s in enumerate(sents)]]
   ...: }

In [6]: response = requests.post(url, data=json.dumps(params), headers=headers).json()

In [7]: for s in response['result']:
   ...:         for sr in s['sent'].split('\n'):
   ...:                 print sr
   ...:         
# S-ID:8 KNP:4.16-CF1.1 DATE:2016/02/06 SCORE:-19.12316
* 1D <文頭><デ><助詞><体言><係:デ格><区切:0-0><格要素><連用要素><正規化代表表記:クロール/くろーる><主辞代表表記:クロール/くろーる>
+ 1D <文頭><デ><助詞><体言><係:デ格><区切:0-0><格要素><連用要素><名詞項候補><先行詞候補><正規化代表表記:クロール/くろーる><解析格:デ>
クロール くろーる クロール 名詞 6 普通名詞 1 * 0 * 0 "代表表記:クロール/くろーる カテゴリ:抽象物 ドメイン:スポーツ" <代表表記:クロール/くろーる><カテゴリ:抽象物><ドメイン:スポーツ><正規化代表表記:クロール/くろーる><文頭><記英数カ><カタカナ><名詞相当語><自立><内容語><タグ単位始><文節始><固有キー><文節主辞>
で で で 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
* 2D <連体修飾><用言:動><係:連格><レベル:B><区切:0-5><ID:（動詞連体）><連体節><動態述語><正規化代表表記:泳ぐ/およぐ><主辞代表表記:泳ぐ/およぐ>
+ 2D <連体修飾><用言:動><係:連格><レベル:B><区切:0-5><ID:（動詞連体）><連体節><動態述語><正規化代表表記:泳ぐ/およぐ><用言代表表記:泳ぐ/およぐ><時制-未来><格関係0:デ:クロール><格関係2:ガ:少女><格解析結果:泳ぐ/およぐ:動16:ガ/N/少女/2/0/8;ヲ/U/-/-/-/-;ニ/U/-/-/-/-;ト/U/-/-/-/-;デ/C/クロール/0/0/8;マデ/U/-/-/-/-;ヘ/U/-/-/-/-;時間/U/-/-/-/-;修飾/U/-/-/-/-;ニナラブ/U/-/-/-/-;外の関係/U/-/-/-/->
泳いで およいで 泳ぐ 動詞 2 * 0 子音動詞ガ行 4 タ系連用テ形 14 "代表表記:泳ぐ/およぐ" <代表表記:泳ぐ/およぐ><正規化代表表記:泳ぐ/およぐ><かな漢字><活用語><自立><内容語><タグ単位始><文節始><文節主辞>
いる いる いる 接尾辞 14 動詞性接尾辞 7 母音動詞 1 基本形 2 "代表表記:いる/いる" <代表表記:いる/いる><正規化代表表記:いる/いる><かな漢字><ひらがな><活用語><付属>
* 3D <SM-主体><SM-人><ヲ><助詞><体言><係:ヲ格><区切:0-0><格要素><連用要素><正規化代表表記:少女/しょうじょ><主辞代表表記:少女/しょうじょ>
+ 3D <SM-主体><SM-人><ヲ><助詞><体言><係:ヲ格><区切:0-0><格要素><連用要素><名詞項候補><先行詞候補><正規化代表表記:少女/しょうじょ><解析連格:ガ><解析格:ヲ>
少女 しょうじょ 少女 名詞 6 普通名詞 1 * 0 * 0 "代表表記:少女/しょうじょ カテゴリ:人" <代表表記:少女/しょうじょ><カテゴリ:人><正規化代表表記:少女/しょうじょ><漢字><かな漢字><名詞相当語><自立><内容語><タグ単位始><文節始><文節主辞>
を を を 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
* -1D <文末><補文ト><時制-過去><用言:動><レベル:C><区切:5-5><ID:（文末）><提題受:30><主節><動態述語><正規化代表表記:見る/みる><主辞代表表記:見る/みる>
+ -1D <文末><補文ト><時制-過去><用言:動><レベル:C><区切:5-5><ID:（文末）><提題受:30><主節><動態述語><正規化代表表記:見る/みる><用言代表表記:見る/みる><主題格:一人称優位><格関係2:ヲ:少女><格解析結果:見る/みる:動5:ガ/U/-/-/-/-;ヲ/C/少女/2/0/8;ニ/U/-/-/-/-;ト/U/-/-/-/-;デ/U/-/-/-/-;カラ/U/-/-/-/-;ヨリ/U/-/-/-/-;マデ/U/-/-/-/-;時間/U/-/-/-/-;外の関係/U/-/-/-/-;修飾/U/-/-/-/-;ノ/U/-/-/-/-;ヲツウジル/U/-/-/-/-;トスル/U/-/-/-/-;ニムケル/U/-/-/-/->
見た みた 見る 動詞 2 * 0 母音動詞 1 タ形 10 "代表表記:見る/みる 補文ト 自他動詞:自:見える/みえる" <代表表記:見る/みる><補文ト><自他動詞:自:見える/みえる><正規化代表表記:見る/みる><文末><表現文末><かな漢字><活用語><自立><内容語><タグ単位始><文節始><文節主辞>

# S-ID:6 KNP:4.16-CF1.1 DATE:2016/02/06 SCORE:-27.36756
* 3D <文頭><デ><助詞><体言><係:デ格><区切:0-0><格要素><連用要素><正規化代表表記:望遠/ぼうえん+鏡/かがみ?鏡/きょう><主辞代表表記:鏡/かがみ?鏡/きょう><主辞’代表表記:望遠/ぼうえん+鏡/かがみ?鏡/きょう>
+ 1D <文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:望遠/ぼうえん>
望遠 ぼうえん 望遠 名詞 6 普通名詞 1 * 0 * 0 "代表表記:望遠/ぼうえん カテゴリ:抽象物" <代表表記:望遠/ぼうえん><カテゴリ:抽象物><正規化代表表記:望遠/ぼうえん><文頭><漢字><かな漢字><名詞相当語><自立><内容語><タグ単位始><文節始>
+ 4D <デ><助詞><体言><係:デ格><区切:0-0><格要素><連用要素><一文字漢字><名詞項候補><先行詞候補><正規化代表表記:鏡/かがみ?鏡/きょう><Wikipedia上位語:装置/そうち><Wikipediaエントリ:望遠鏡><解析格:デ>
鏡 かがみ 鏡 名詞 6 普通名詞 1 * 0 * 0 "代表表記:鏡/かがみ 漢字読み:訓 カテゴリ:人工物-その他 ドメイン:家庭・暮らし" <代表表記:鏡/かがみ><漢字読み:訓><カテゴリ:人工物-その他><ドメイン:家庭・暮らし><正規化代表表記:鏡/かがみ?鏡/きょう><品曖><ALT-鏡-きょう-鏡-6-1-0-0-"代表表記:鏡/きょう 漢字読み:音 カテゴリ:人工物-その他"><品曖-普通名詞><原形曖昧><Wikipedia上位語:装置/そうち:0-1><Wikipediaエントリ:望遠鏡:0-1><漢字><かな漢字><名詞相当語><自立><複合←><内容語><タグ単位始><文節主辞><名詞曖昧性解消>
で で で 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
* 2D <連体修飾><用言:動><係:連格><レベル:B><区切:0-5><ID:（動詞連体）><連体節><動態述語><正規化代表表記:泳ぐ/およぐ><主辞代表表記:泳ぐ/およぐ>
+ 3D <連体修飾><用言:動><係:連格><レベル:B><区切:0-5><ID:（動詞連体）><連体節><動態述語><正規化代表表記:泳ぐ/およぐ><用言代表表記:泳ぐ/およぐ><時制-未来><格関係3:ガ:少女><格解析結果:泳ぐ/およぐ:動16:ガ/N/少女/3/0/6;ヲ/U/-/-/-/-;ニ/U/-/-/-/-;ト/U/-/-/-/-;デ/U/-/-/-/-;マデ/U/-/-/-/-;ヘ/U/-/-/-/-;時間/U/-/-/-/-;修飾/U/-/-/-/-;ニナラブ/U/-/-/-/-;外の関係/U/-/-/-/->
泳いで およいで 泳ぐ 動詞 2 * 0 子音動詞ガ行 4 タ系連用テ形 14 "代表表記:泳ぐ/およぐ" <代表表記:泳ぐ/およぐ><正規化代表表記:泳ぐ/およぐ><かな漢字><活用語><自立><内容語><タグ単位始><文節始><文節主辞>
いる いる いる 接尾辞 14 動詞性接尾辞 7 母音動詞 1 基本形 2 "代表表記:いる/いる" <代表表記:いる/いる><正規化代表表記:いる/いる><かな漢字><ひらがな><活用語><付属>
* 3D <SM-主体><SM-人><ヲ><助詞><体言><係:ヲ格><区切:0-0><格要素><連用要素><正規化代表表記:少女/しょうじょ><主辞代表表記:少女/しょうじょ>
+ 4D <SM-主体><SM-人><ヲ><助詞><体言><係:ヲ格><区切:0-0><格要素><連用要素><名詞項候補><先行詞候補><正規化代表表記:少女/しょうじょ><解析連格:ガ><解析格:ヲ>
少女 しょうじょ 少女 名詞 6 普通名詞 1 * 0 * 0 "代表表記:少女/しょうじょ カテゴリ:人" <代表表記:少女/しょうじょ><カテゴリ:人><正規化代表表記:少女/しょうじょ><漢字><かな漢字><名詞相当語><自立><内容語><タグ単位始><文節始><文節主辞>
を を を 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
* -1D <文末><補文ト><時制-過去><用言:動><レベル:C><区切:5-5><ID:（文末）><提題受:30><主節><動態述語><正規化代表表記:見る/みる><主辞代表表記:見る/みる>
+ -1D <文末><補文ト><時制-過去><用言:動><レベル:C><区切:5-5><ID:（文末）><提題受:30><主節><動態述語><正規化代表表記:見る/みる><用言代表表記:見る/みる><主題格:一人称優位><格関係1:デ:鏡><格関係3:ヲ:少女><格解析結果:見る/みる:動5:ガ/U/-/-/-/-;ヲ/C/少女/3/0/6;ニ/U/-/-/-/-;ト/U/-/-/-/-;デ/C/鏡/1/0/6;カラ/U/-/-/-/-;ヨリ/U/-/-/-/-;マデ/U/-/-/-/-;時間/U/-/-/-/-;外の関係/U/-/-/-/-;修飾/U/-/-/-/-;ノ/U/-/-/-/-;ヲツウジル/U/-/-/-/-;トスル/U/-/-/-/-;ニムケル/U/-/-/-/->
見た みた 見る 動詞 2 * 0 母音動詞 1 タ形 10 "代表表記:見る/みる 補文ト 自他動詞:自:見える/みえる" <代表表記:見る/みる><補文ト><自他動詞:自:見える/みえる><正規化代表表記:見る/みる><文末><表現文末><かな漢字><活用語><自立><内容語><タグ単位始><文節始><文節主辞>
```

### サーバ停止
```
$ fab config:'deploy.properties' stopproxy -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' stopAllworkers -u root -i ~/.ssh/docker_rsa
$ fab config:'deploy.properties' stopAllservers -u root -i ~/.ssh/docker_rsa
```
