*******************CS242 Information Retrieval Project Phase 2 ************* 
This file contains the details about the folders structure
1. Project Workspace - contains the generated workspace for Search Engine.
2. Project Workspace Hadoop - contains the generated workspace for Map Reduce Program for generating Inverted Index 
3. src
    > SearchLuceneIndex.java 		- REST API Service for both Lucene and Hadoop
	> TweetResponse.java 	 	- Class template for Tweet Response
	> TweetSearcher.java     	- Searcher the results in lucene generated index files are returns results based on the keyword passed in
	> ConvertToBigJSON.java  	- Reads generated inverted index JSON from Hadoop and generates a Big JSON.
	> CreateJSONFromMangoDB.java	- Reads tweets from MongoDB and generates JSON per tweet holding docID, Tweet, HashTag as JSON attributes
	> WriteTweetToMongoDB.java 	- Hold the logic to write the crawler tweets into MongoDB
	> InvertedIndexGenerator.java - Holds the logic to generate Inverted Index using Map Reduce 
	> techSearch.html		- Web page 
	> tweetSearch.js		- Javascript file 

4. Report - contains project report 
------------------------------------------------------------------------------------------
How to run the project?
> Make sure Apache Tomcat v9.016 is installed in eclipse
> Import the folder named "InitiateSearch" into eclipse
> Right click on "SearchLuceneIndex.java" and run on Server
> Go to any browser and type in the following URL:
	> URL : http://localhost:8080/InitiateSearch/
----------------------------------------------------------------------------------------------