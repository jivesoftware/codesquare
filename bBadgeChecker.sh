#!/bin/sh

#{ "cID": "607280d89776dcde41f953d3c7b7f4ac4f022565", "cMes": "test-IP-stacking-commits", "date": "Fri Jul 8 10:17:58 2011 -0700", "name": "Eric Ren", "email": "eric.ren@jivesoftware.com", "stats": "[ 1 0 sample ]" }

function genRanStr
{
    str=""

    characters="0123456789abcdefghijklmnopqrstuvwxyz"
    letlength=${#characters}

    while [ ${#str} -lt 40 ]
    do
	rcl=`expr $RANDOM % $letlength`
	character=${characters:$rcl:1} 
	str+=$character
    done
    echo $str
    return
}
commitID=`genRanStr`
commitEmail=$1
commitMsg=$2
isotimestamp=$3
unixtimestamp=`date --date="$isotimestamp" +%s`
commitName=$4
stats=$5
pushDate=`date`
times=$6

if [ $# -lt 5 ]; then
    echo ERRRGLANG! That is my way of saying you did something WRRRRONG! Must have at least 6 arguments and no more than 7, all enclosed as strings
    exit 3
fi
    
if [ $# -gt 6 ]; then
    echo ERRRGLANG! That is my way of saying you did something WRRRRONG! Must have at least 6 arguments and no more than 7, all enclosed as strings
fi

if [ $# == 5 ]; then
    times=1
fi

jCommit=`echo [{ "\"cID\"": \"$commitID\", "\"cMes\"": \"$commitMsg\", "\"unixtimestamp\"": \"$unixtimestamp\", "\"isotimestamp\"": \"$isotimestamp\","\"name\"": \"$commitName\", "\"email\"": \"$commitEmail\", "\"stats\"": \"$5\" }]`
echo Posting the following JSON to our BackEndServlet:
echo $jCommit

param3=`echo \"json=$jCommit\"`

unixTime=`date +%s`
timeZone=`date +%z`

`curl -v --data-urlencode "timeZone=$timeZone" --data-urlencode "unixTime=$unixTime" --data-urlencode "json=$jCommit" http://10.45.111.143:9090/CodeSquare/BackEndServlet`

echo Done!