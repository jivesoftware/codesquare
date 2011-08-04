#!/bin/sh



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

function getUNIXTime #only works on a LINUX machine
{
    uyear=$1
    umonth=$2
    uday=$3
    uhour=$4
    uminute=$5
    yrstr=$uyear
    yrstr+="-"
    yrstr+=$umonth
    yrstr+="-"
    yrstr+=$uday
    yrmonsec=`date -d $yrstr "+%s"` 
    hrsec=`expr $uhour \* 3600`
    minsec=`expr $uminute \* 60`
    sumsec=`expr $yrmonsec + $hrsec + $sumsec`
    echo $sumsec
    return
}

#debugging delete these later
stringy=`genRanStr`
usery=`randUsr`
bossy=`randUsrBoss $usery`
echo $stringy
echo $usery
echo $bossy
#End debugging

argNum=$#

case $argNum in
    1) times=$1
       year=`getYear`
       month=`randMonth`
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`;;
    2) times=$1
       year= $2
       month=`randMonth`
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`;;
    3) times=$1
       year=$2
       month=$3
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`;;
    4) times=$1
       year=$2
       month=$3
       day=$4
       hour=`randHour`
       minute=`randMinute`;;
    5) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=`randMinute`;;
    6) times=$1
       year=$2
       month=$3
       day=$4
       hour=$5
       minute=$6;;
    *) times=1
       year=`getYear`
       month=`randMonth`
       day=`randDay`
       hour=`randHour`
       minute=`randMinute`;;
esac

#Debugging again delete later
echo $times
echo $year
echo $month
echo $day
echo $hour
echo $minute
#End Debugging again

echo `getUNIXTime $year $month $day $hour $minute`

