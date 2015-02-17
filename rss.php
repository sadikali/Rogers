<?php

//The feed that we're going to read
$rss = simplexml_load_file('http://www.macleans.ca/multimedia/feed/');

//Connect to the database
$mysqli = new mysqli('localhost', 'rssfeed', 'rssfeed', 'rssdata');
if ($mysqli->connect_errno) {
    echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
    exit(0);
}

//Loop through each item.
//
// Get the title, comment, link, guid, description and publication date, sanitize and add quotes to the string

foreach ($rss->channel->item as $item) {
	
	$title = (isset($item->title))? "'" . $mysqli->escape_string($item->title) . "'" : "NULL";
	$comment = (isset($item->comment))? "'" . $mysqli->escape_string($item->comment) . "'" : "NULL";
	$link = (isset($item->link))?  "'" . $mysqli->escape_string($item->link) . "'" : "NULL";
	$guid = (isset($item->guid))?  "'" . $mysqli->escape_string($item->guid) . "'" : "NULL";
	$description = (isset($item->description[0])) ?  "'" . $mysqli->escape_string($item->description[0]) . "'" : "NULL";
	$pubDate = (isset($item->pubDate)) ? "'" . $mysqli->escape_string($item->pubDate)  . "'" : "NULL";

	
	//Call the stored procdure to add the item to the database
	if(!$mysqli->multi_query( "CALL addItem ($title, $comment, $link, $guid, $description, $pubDate, @id);SELECT @id as id"))
		echo "ERROR: " . $mysqli->error . "\n";	

	//Retrieve the ID from the previous call
	$mysqli->next_result();
	$result = $mysqli->store_result();
	$dataID = $result->fetch_object()->id;
	$result->free();	
	if ($dataID == 0)
	{
		//echo "We've already processed this record! guid = " . $guid;
		break;
	}	
	//Loop through the category and save to the database, using the previous item ID	
	foreach ($item->category as $data)
	{
		$clean = "'" . $mysqli->escape_string($data) . "'";
		if(!$mysqli->multi_query("CALL addCategory($clean, $dataID);"))
			echo "ERROR : " . $mysqli->error;
		
	}
}

//
// Part 2 read from the database and create JSON
//


//Get all the items from the RSS feed
$mysqli_result = $mysqli->query("SELECT * FROM item");
$rtn;

//Loop through if we've got some results
if ($mysqli_result)
{
        for($i = 0; $i < $mysqli_result->num_rows; $i ++)
        {
                //Get this item
                $item = $mysqli_result->fetch_assoc();
                
                //Get all the categories associated with this item      
                if ($mysql_cat = $mysqli->query("SELECT name FROM category_item join category using(category_id) where item_id = " . $item['item_id']))
                        $category = $mysql_cat->fetch_all();
                else
                {
                        echo "ERROR failed to get categories " . $mysqli->error . "\n";
                        exit(0);
                }

                //Store the categories with this item   
                $item['category'] = $category;

                //Remove the item id (this is for our internal use only, so no need to send it to the world)
                unset($item['item_id']);
                
                //Add it to the result set
                $rtn[$i] = $item;
        }

        //json encode and send it off!
        echo json_encode($rtn);
}
else
{
        echo "ERROR failed to get items " . $mysqli->error . "\n";
}


?>
