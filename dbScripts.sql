CREATE DATABASE IF NOT EXISTS rssdata;
USE rssdata;
CREATE TABLE IF NOT EXISTS category
(
	category_id INT AUTO_INCREMENT PRIMARY KEY,
	name varchar(30) UNIQUE 
);

CREATE TABLE IF NOT EXISTS item
(
	item_id INT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(100),
	comment TEXT,
	link VARCHAR(200),
	guid VARCHAR(100),
	description TEXT,
	pubDate DATETIME 
); 

CREATE TABLE IF NOT EXISTS category_item
(
	item_id INT, 
	category_id INT, 
	FOREIGN KEY (item_id) REFERENCES item(item_id),
	FOREIGN KEY (category_id) REFERENCES category(category_id)
);

DELIMITER $$

-- Procedure to add an item from an RSS feed
--
-- Parameters 
--	IN:
--		title
--		comment
--		link
--		guid
--		description
--		publication date
--	OUT: 
--		id of the item just inserted, set to 0 if this record already exists

CREATE PROCEDURE addItem (IN title VARCHAR(100), IN comment TEXT, IN link VARCHAR(200), IN guid VARCHAR(100), IN description TEXT, IN pubDate VARCHAR(100), OUT id INT)
BEGIN
	DECLARE pDate DATETIME DEFAULT NULL;
	DECLARE tempID INT DEFAULT NULL;
	SELECT 0 INTO id;

	SELECT item_id INTO tempID FROM item i WHERE i.guid = guid;
	IF tempID IS NULL THEN 
		IF pubDate IS NOT NULL THEN
			SELECT STR_TO_DATE(pubDate, "%a, %e %b %Y %T +0000") INTO pDate;
		END IF;
		INSERT INTO item VALUES (NULL, title, comment, link, guid, description, pDate);
		SELECT LAST_INSERT_ID() INTO id;
	END IF;
END $$


-- Procedure to add a category to an item
-- Checks to see if the category exists and inserts it if it doesn't
-- Links the category and the item in the category_item table.
--
-- Parameters:
--	name of the category
--	id of the item associated with this category

CREATE PROCEDURE addCategory (IN name_in VARCHAR(100), IN item_ID INT)
BEGIN
	DECLARE id_category INT DEFAULT NULL;
	SELECT category_id INTO id_category FROM category WHERE name = name_in;
	IF id_category IS NULL THEN
		INSERT INTO category VALUES (NULL, name_in);
		SELECT LAST_INSERT_ID() INTO id_category;
	END IF;
	INSERT INTO category_item VALUES (item_ID, id_category);
END $$

DELIMITER ;


