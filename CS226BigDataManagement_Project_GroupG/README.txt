-----------------------------------------------------------------------------------------------------
Analysis of Business Trends using Yelp!
-----------------------------------------------------------------------------------------------------
* This File holds instruction about 
	> How to run the code Files
	> Project directory structure details

***********************
* Directory Structure *
***********************
 > CodeFiles
	> StarDistribution.py - Holds PySpark Script to generate star disttibution data and write the output to CSV file  
	> PopularBusinessCategories.py - Holds PySpark Script to generate popular Business Categories in USA and write the output to CSV file
	> TopCities.py - Holds PySpark Script to generate Top Cities with business in USA and write the output to CSV file
	> GenerateScatterPointDataVegas.py - Holds PySpark Script to Generate Scatter Plot Data i.e (Lat and Long) and write the output to CSV File
	> GenerateScatterPointDataPheonix.py - Holds PySpark Script to Generate Scatter Plot Data i.e (Lat and Long) and write the output to CSV File
	> GenerateVegasHeatMapData.py - Holds PySpark Script to Generate Vegas Heat Map Data i.e (Lat and Long) and write the output to CSV File\
        > ProjectVisualizations.ipynb - Jupyter notebook holding all the visualizations 
 > Results
	> StarDistribution.csv - Holds Output of  StarDistribution.py 
	> PopularBusinessCategories.csv - Holds Output of  PopularBusinessCategories.py 
	> TopCities.csv	- Holds Output of  TopCities.py 
	> VegasScatterPlot.csv - Holds Output of  GenerateScatterPointDataVegas.py for Vegas
	> PhoenixScatterPlot.csv - Holds Output of  GenerateScatterPointDataPheonix.py for Phoenix 
	> VegasHeatMapData.txt - Holds Output of  GenerateVegasHeatMapData.py
 > Visualizations
	> StarDistribution - Visualization for StarDistribution
	> PopularBusinessCategories - Visualization for PopularBusinessCategories.py 
	> TopCities - Visualization for TopCities
 	> VegasScatterPlot - Visualization for VegasScatterPlot
	> PhoenixScatterPlot- Visualization for PhoenixScatterPlot
	> VegasHeatMap_Animation - Animated Visualization for GenerateVegasHeatMapData
 > Presentation_Project_GroupG 
      > Presentation about the project
 > Report
      > Detailed Project Report

******************************************
* How to Run the code Execution - Steps ?*
******************************************
> Open PySpark Shell 
> Run  PySpark Script using the following command
	> python fileName.py
> look for the output in hadoop distributed file System

*****************************************************************************************************************************************
Note:
> Please ensure to change the directory of the input file accordingly based on your local file system
> Please ensure to install necessary python libraries like numpy, MatPlotLib, Folium.