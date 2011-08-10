echo Welcome to the CodeSquareApp Badge Creation Assistant!
echo Enter the badge number:
read bNum
echo Enter the badge name:
read bName
echo Enter the badge description:
read bDesc
echo NOTE: Make Sure the Badge Pic is located in the images folder of the App and it is a png whose name is the badge number Ex images/31.png
echo ""
echo The hbase shell is about to open, when it does, please input the following commands each followed by a return to input the appropriate badge information. After this your badge is now part of the codesquare app! Enter the command exit to get out of the hbase shell
echo put \'Badges\', \'$bNum\', \'Info:name\', \'$bName\'
echo put \'Badges\', \'$bNum\', \'Info:description\', \'$bDesc\'
echo put \'Badges\', \'$bNum\', \'Info:iconURL\', \'images/$bNum.png\'
hbase shell
