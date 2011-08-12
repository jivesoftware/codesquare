function getLines
{
    echo $1
    return
}

lines=`getLines \`wc -l testCommitLog.txt\``
echo $lines

echo Welcome to the CodeSquareApp Badge Creation Assistant!
echo Enter the badge name:
read bName
echo Enter the badge description:
read bDesc
echo NOTE: Make Sure the Badge Pic is located in the images folder of the CodeSquare App and it is a png whose name is the badge number Ex images/31.png