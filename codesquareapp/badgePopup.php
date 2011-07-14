<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
   <link rel="stylesheet" href="popup.css" type="text/css" media="screen" />
</head>
<body> 
<?php 

$imgURL = $_POST['imgURL'];
$name = $_POST['name'];
$desc = $_POST['desc'];


echo "<img src='$imgURL'/>";
echo "<h1 class='center'>$name</h1>";
echo "<p class='center'>$desc</p>";

?>
</body>
</html>
