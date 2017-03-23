<?php

	$con = mysqli_connect ( "127.0.0.1", "root", "0000", "db" );
	
	mysqli_set_charset ( $con, "utf8" );

	if (mysqli_connect_errno ( $con )) {
		echo "Failed to connect to MySQL: " . mysqli_connect_error ();
	}
	
	$groupname = $_POST['groupname'];
	
 	//$selectByGroupName = mysqli_query($con,"select * from mylist where groupname = '$groupname'");
	$selectByGroupName = mysqli_query($con,"SELECT name,sum(count) AS count,groupname FROM mylist where groupname = '$groupname' GROUP BY name,groupname ORDER BY seq asc");
	
	$result = array();
	
	while($row = mysqli_fetch_array($selectByGroupName)){
		
		array_push($result,array('name'=>$row[0],'count'=>$row[1],'groupname'=>$row[2]));
	}
	
	$json = json_encode(array("result"=>$result));
	 
	echo $json;
	
	mysqli_close($con);

?>