<?php
$con = mysqli_connect ( "127.0.0.1", "root", "0000", "db" );

mysqli_set_charset ( $con, "utf8" );

if (mysqli_connect_errno ( $con )) {
	echo "Failed to connect to MySQL: " . mysqli_connect_error ();
}

$name = $_POST ['name'];
$count = $_POST ['count'];
$groupname = $_POST ['groupname'];

$result = mysqli_query ( $con, "insert into mylist (name,count,groupname) values ('$name','$count','$groupname')");

if ($result) {
	echo "$name Entered.";
} else {
	mysqli_query ( $con, "insert into mylist (name,count,groupname) values ('$name','$count','$groupname')");
	echo 'Failure';
}

mysqli_close ( $con );

?>
