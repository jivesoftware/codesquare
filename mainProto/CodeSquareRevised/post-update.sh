#!/bin/sh

email=`git config user.email`
beforeDate=`date`
# get from jar
afterDate="Wed Jul 20 11:24:24 2011 -0700"
email=justin.kikuchi@jivesoftware.com
unixTime=`date +%s`
timeZone=`date +%z`

com=`git log --pretty=format:']"},{ "cID": "%H", "cMes": "%f", "unixtimestamp": "%ct", "isotimestamp": "%ci", "name": "%cn", "email": "%ce", "stats": "[' --numstat --before="$beforeDate" --after="$afterDate"  --committer=$email`
com="["${com:4}"]\"}]"; 
x=`echo $com`

`curl -v --data-urlencode "timeZone=$timeZone" --data-urlencode "unixTime=$unixTime" --data-urlencode "json=$x" http://10.45.111.143:9090/CodeSquare/BackEndServlet`

#git log --pretty=format:']"},{ "cID": "%H", "cMes": "%f", "unixtimestamp": "%ct", "isotimestamp": "%ci", "name": "%cn", "email": "%ce", "stats": "[' --numstat --before="Wed Jul 27 10:46:33 PDT 2011" --after="Tue Jul 27 07:59:13 2011"  --committer=justin.kikuchi@jivesoftware.com

