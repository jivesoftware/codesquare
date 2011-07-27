#!/bin/sh

email=`git config user.email`
beforeDate=`date`
# get from jar
afterDate="Tue Jul 25 07:59:13 2011"
email=justin.kikuchi@jivesoftware.com
unixTime=`date +%s`
timeZone=`date +%z`

com=`git log --pretty=format:']"},{ "cID": "%H", "cMes": "%f", "unixtimestamp": "%ct", "isotimestamp": "%ci", "name": "%cn", "email": "%ce", "stats": "[' --numstat --before="$beforeDate" --after="$afterDate"  --committer=$email`
com="["${com:4}"]\"}]"; 
x=`echo $com`

`curl -v --get --data-urlencode "timeZone=$timeZone" --data-urlencode "unixTime=$unixTime" --data-urlencode "json=$x" http://10.45.111.143:9090/CodeSquareTesting/BackEndServlet`

git log --pretty=format:']"},{ "cID": "%H", "cMes": "%f", "unixtimestamp": "%ct", "isotimestamp": "%ci", "name": "%cn", "email": "%ce", "stats": "[' --numstat --before="Wed Jul 27 10:46:33 PDT 2011" --after="Tue Jul 27 07:59:13 2011"  --committer=justin.kikuchi@jivesoftware.com

