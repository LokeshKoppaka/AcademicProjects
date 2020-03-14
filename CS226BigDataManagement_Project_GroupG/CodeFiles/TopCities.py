import pyspark.sql.functions as f

#import yelp_business.csv as dataframe
yelp_b = spark.read.option("header","true").option("quote","\"").option("escape","\"").csv("/Project_BigData/Data/yelp_business.csv")
# Logic to retrieve sum of business review per city
yelp_cities = yelp_b.select('city', yelp_b.review_count.cast("int")).groupBy(yelp_b.city).sum().orderBy('sum(review_count)', ascending = False)
yelp_cities.write.csv("/TopCities") # Logic to write the output as CSV