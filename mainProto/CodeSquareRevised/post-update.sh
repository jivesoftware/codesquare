#!/bin/sh

firstId=23l4asdfjkl2390dfsjkl290dfs
newId=12345asdf

email=`git show $firstId --format=%ce -s`
lastId=`curl -v --data-urlencode "email=$email" --data-urlencode "firstId=$firstId" --data-urlencode "newId=$newId" http://10.45.111.143:9090/CodeSquare/BackEndServlet`
echo $lastId

unixTime=`date +%s`
timeZone=`date +%z`

com=`git log --pretty=format:']"},{ "cID": "%H", "cMes": "%f", "unixtimestamp": "%ct", "isotimestamp": "%ci", "name": "%cn", "email": "%ce", "stats": "[' ^$lastId --all --graph --committer=$email`
com="["${com:4}"]\"}]"; 
x=`echo $com`

`curl -v --data-urlencode "timeZone=$timeZone" --data-urlencode "unixTime=$unixTime" --data-urlencode "json=$x" http://10.45.111.143:9090/CodeSquare/BackEndServlet`

#git log --pretty=format:']"},{ "cID": "%H", "cMes": "%f", "unixtimestamp": "%ct", "isotimestamp": "%ci", "name": "%cn", "email": "%ce", "stats": "[' --numstat --before="Wed Jul 27 10:46:33 PDT 2011" --after="Tue Jul 27 07:59:13 2011"  --committer=justin.kikuchi@jivesoftware.com

