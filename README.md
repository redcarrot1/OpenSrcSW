# OpenSrcSW
2022-1 오픈소스 SW 프로젝트

# 컴파일 명령어
`javac -cp jars/jsoup-1.14.3.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`

---

# 실행 명령어
#### 2주차
`java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -c html`
<br>

#### 3주차
`java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -k collection.xml`
<br>

#### 4주차
`java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -i index.xml`
<br>

#### 5주차

`java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -s index.post -q "yourQuery"`
<br>



#### 전체 주차(파일은 defalut로 세팅됩니다.)
`java -cp ./jars/jsoup-1.14.3.jar:./jars/kkma-2.1.jar:bin scripts.kuir -a /`
