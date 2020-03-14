import pyspark.sql.functions as f

# import yelp_business.csv as dataframe
yelp_b = spark.read.option("header","true").option("quote","\"").option("escape","\"").csv("/Project_BigData/Data/yelp_business.csv")
yelp_b = yelp_b.groupBy("star").count() # groupBy stars and count per aggregation which generates stars distribution
yelp_b.write.csv("/StarDistribution") # Logic to write the output as CSV