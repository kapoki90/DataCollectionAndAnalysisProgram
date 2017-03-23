<?php

function unistr_to_xnstr($str){
	return preg_replace('/\\\u([a-z0-9]{4})/i', "&#x\\1;", $str);
}

$con=mysqli_connect("127.0.0.1","root","0000","db");

if (mysqli_connect_errno($con))
{
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}


mysqli_set_charset($con,"utf8");


$res = mysqli_query($con,"SELECT name,sum(count) AS count,groupname 
		FROM mylist GROUP BY name,groupname");

$result = array();

while($row = mysqli_fetch_array($res)){
	//add 1 element value
	array_push($result,array('name'=>$row[0],'count'=>$row[1],'groupname'=>$row[2]));
}

$json = json_encode(array("result"=>$result));
//$json = json_encode(array("result"=>$result));

echo $json;


mysqli_close($con);

?>