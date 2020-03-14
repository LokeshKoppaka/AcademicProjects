import pyspark.sql.functions as f

#import yelp_business.csv as dataframe
yelp_b = spark.read.option("header","true").option("quote","\"").option("escape","\"").csv("/Project_BigData/Data/yelp_business.csv")
# Lat and Lon for Las Vegas city
lat = 36.127430 
lon = -115.138460
# Limit the Lat and Lon for easy visulization of results
lon_min, lon_max = lon-0.3,lon+0.5
lat_min, lat_max = lat-0.4,lat+0.5
# Logic to filter only Las Vegas records
yelp_LV = yelp_b.select("city", yelp_b.latitude.cast("double"), yelp_b.longitude.cast("double"), yelp_b.stars.cast("double")).filter(yelp_b.city == "Las Vegas")
# Logic to select only records which are within the configured Lat and Lon Limit
yelp_LV_PlotInfo = yelp_LV.withColumn('plotOnMap',(yelp_LV.latitude > lat_min) & (yelp_LV.latitude < lat_max) & (yelp_LV.longitude > lon_min) & (yelp_LV.longitude < lon_max))
yelp_LV_PlotInfo_True = yelp_LV_PlotInfo.filter(yelp_LV_PlotInfo.plotOnMap == "true")
yelp_LV_PlotInfo_True.write.csv("/VegasScatterPlot") # Logic to write the output as CSV

