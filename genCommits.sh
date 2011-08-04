#!/bin/sh

#WARNING: THIS SCRIPT WILL ONLY RUN FLAWLESSLY ON A LINUX MACHINE

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


function randUsr
{
    #Email variables
    demail=diivanand.ramalingam@jivesoftware.com # Diiv email
    jemail=justin.kikuchi@jivesoftware.com #Justin email
    semail=deanna.surma@jivesoftware.com #Deanna email
    eemail=eric.ren@jivesoftware.com #Eric email
    
    num=`expr $RANDOM % 4`
    if [ $num == 0 ]
    then
	echo diivanand.ramalingam@jivesoftware.com
    elif [ $num == 1 ]
    then
	echo justin.kikuchi@jivesoftware.com
    elif [ $num == 2 ]
    then
	echo deanna.surma@jivesoftware.com
    else
	echo eric.ren@jivesoftware.com
    fi
    return
}

function randUsrBoss
{
    if [ $1 = diivanand.ramalingam@jivesoftware.com -o $1 = justin.kikuchi@jivesoftware.com -o $1 = deanna.surma@jivesoftware.com ]; then
	echo eric.ren@jivesoftware.com
    elif [ $1 = eric.ren@jivesoftware.com ]
    then 
	echo ""
    else
	echo "errrrrglanginvaliduser@wrong.com"
    fi
    return
}

function getYear
{
    echo `date +"%Y"`
    return
}

function randMonth
{
    rmonth=`expr $RANDOM % 12`
    echo `expr $rmonth + 1`
    return
}

function randDay
{
    rday=`expr $RANDOM % 28`
    echo `expr $rday + 1`
    return
}

function randHour
{
    rhour=`expr $RANDOM % 24`
    echo $rhour
    return
}

function randMinute
{
    rminute=`expr $RANDOM % 60`
    echo $rminute
    return
}

function randSec
{
    rsec=`expr $RANDOM % 60`
    echo $rsec
    return
}

function defTZ
{
    echo GMT-07:00
    return
}

function testFC
{
    if [ $# == 0 ]
    then
	type=1
    else
	type=$1
    fi
    case $type in
	0) stringy="0 []";;
	1) stringy="1 [app.xml]";;
	2) stringy="2 [/home/app.xml,/home/hello.html]";;
	3) stringy="3 [app.xml,home/hello.html,home/hi.jsp]";;
	*) stringy="0 []";;
    esac
    echo $stringy
    return
}

function testMsg
{
    if [ $# == 0 ]
    then
	type=1
    else
	type=$1
    fi
	
    case $type in
	0) stringy="\"\"";;
	1) stringy="\"This is a Jive test commit\"";;
	*) stringy="\"This is a Jive test commit\"";;
    esac
    echo $stringy
    return
}

function insDels
{
    command=$1
    
    case $command in
	0) stringy="10 0";;
	1) stringy="0 10";;
	2) stringy="0 0";;
	3) stringy="3 3";;
	*) stringy="5 5";;
    esac
    echo $stringy
    return
}

function getUNIXTime #only works on a LINUX machine
{
    uyear=$1
    umonth=$2
    uday=$3
    uhour=$4
    uminute=$5
    usec=$6
    yrstr=$uyear
    yrstr+="-"
    yrstr+=$umonth
    yrstr+="-"
    yrstr+=$uday
    yrmonsec=`date -d $yrstr "+%s"`
    hrsec=`expr $uhour \* 3600`
    minsec=`expr $uminute \* 60`
    sumsec=`expr $yrmonsec + $hrsec + $minsec + $usec`
    echo $sumsec
    return
}

function defArgs
{
    if [ $# == 0 ]
    then
	num1=`expr $RANDOM % 4`
	num2=`expr $RANDOM % 4`
	num3=`expr $RANDOM % 2`
	fcArg=$num1
	insArg=$num2
	msgArg=$num3
    else
	stringToParse=$1
	fcArg=`echo ${stringToParse:0:1}`
	insArg=`echo ${stringToParse:1:1}`
	msgArg=`echo ${stringToParse:2:1}`
    fi
    fcVal=`testFC $fcArg`
    insVal=`insDels $insArg`
    msgVal=`testMsg $msgArg`
    echo $fcVal $insVal $msgVal
    return
}

function makeFolder
{
    year=$1
    year+="/"
    month=$2
    month+="/"
    day=$3
    day+="/"
    hour=$4
    hour+="/"
    minute=$5
    minute+="/"
    start="TestCommits/"
    filePathString=$start$year$month$day$hour$minute
    mkdir -p $filePathString
    file=$6
    filePathString+=$file
    touch $filePathString
    echo $filePathString
    return
}

function createCommit
{
    times=$1
    year=$2
    month=$3
    day=$4
    hour=$5
    minute=$6
    second=$7
    timezone=$8
    args=$9

    if [ ${#args} -le 1 ]
    then
	args=`defArgs`
    fi

    for(( i=0; i<$times; i++ ))
    do
	unixTime=`getUNIXTime $2 $3 $4 $5 $6 $7`
	commitID=`genRanStr`
	commitIDFileString=$commitID
	commitIDFileString+=".txt"
	emailString=`randUsr`
	fullFilePath=`makeFolder $year $month $day $hour $minute $commitIDFileString`
	commitOb=`echo $commitID $emailString $unixTime $timezone $args`
	echo $commitOb > $fullFilePath
	echo $fullFilePath " written"
	year=`getYear`
	month=`randMonth`
	day=`randDay`
	hour=`randHour`
	minute=`randMinute`
	second=`randSec`
	tz=`defTZ`
	args=`defArgs`
    done
    return
}

argNum=$#

case $argNum in
    1) times=$1
       year=`getYear`
       month=`randMonth`
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
    2) times=$1
       year= $2
       month=`randMonth`
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
    3) times=$1
       year=$2
       month=$3
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
    4) times=$1
       year=$2
       month=$3
       day=$4
       hour=`randHour`
       minute=`randMinute`
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
    5) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=`randMinute`
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
    6) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=$6
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
    7) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=$6
       second=$7
       tz=`defTZ`
       args=`defArgs`;;
    8) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=$6
       second=$7
       tz=$8
       args=`defArgs`;;
    9) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=$6
       second=$7
       tz=$8
       args=`defArgs $9`;;
    *) times=1
       year=`getYear`
       month=`randMonth`
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`
       second=`randSec`
       tz=`defTZ`
       args=`defArgs`;;
esac

createCommit $times $year $month $day $hour $minute $second $tz $args
prompt="Successfully created "
prompt+=$times
prompt+=" test commits"
echo $prompt
echo "Copying to HDFS"
hadoop fs -copyFromLocal TestCommits /user/interns/
echo "Deleting local copy"
rm -r TestCommits
echo "Done!"



