import pyspark.sql.functions as f	
						
#import yelp_business.csv as dataframe			
yelp_b = spark.read.option("header","true").option("quote","\"").option("escape","\"").csv("/Project_BigData/Data/yelp_business.csv")			
#yelp_diff categories has rows which has distinct columns as category								
yelp_diff_categories = yelp_b.select("business_id", f.split("categories", ";" ).alias("categories"),f.posexplode(f.split("categories", ";")).alias("pos","val"))								
#Shows all the categories with count in descending order				
yelp_categories = yelp_diff_categories.groupBy("val").count().orderBy("count", ascending = False)
yelp_categories.write.csv("/PopularBusinesses") # Logic to write the output as CSV