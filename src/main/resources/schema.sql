CREATE TABLE IF NOT EXISTS File (
    id     			INTEGER PRIMARY KEY AUTO_INCREMENT,
    file_name	    VARCHAR(100) ,
    original_name	VARCHAR(100) ,
    download_count	INTEGER
)