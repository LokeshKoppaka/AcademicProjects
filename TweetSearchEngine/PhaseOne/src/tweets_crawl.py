import tweepy
from tweepy.streaming import StreamListener
from tweepy import OAuthHandler
from tweepy import Stream
import sys
import argparse
import json
import urllib2
from httplib import BadStatusLine
from lxml.html import parse
import time
import math
import re
from multiprocessing.dummy import Pool as Thread

#twitter credentials
consumer_key = "sDnZkkvx0lERUTowjuqtlBqAQ"
consumer_secret = "AKfTuuhfGXtTNP9cW8q99UVqPwkpTUGm0v1PTROIhKn4UN5Oa1"
access_token = "2178235704-gTkPvuy8t0CaX7QzMZp4RuC6A1QZsYT5V40vtuA"
access_secret = "yhlij3zwwCMMCZkxgfnl6R5OsrDanFpgPokqvyUf0LicY"

#arguments
dirName = str(sys.argv[1]) #data path
numTweets = int(sys.argv[2]) #num of tweets 

tweetcnt = 0
filecnt = 0
file_output_path = dirName+"/tweets_data{0}.txt".format(filecnt)
f = open(file_output_path, 'a')
chkFlag = True

#twitter listener
class twitterListener(StreamListener):

    def on_connect(self):
        print ".... Connected to twitter streaming API ...."
    
    def on_data(self, data):
        global f
        global filecnt
        global tweetcnt
        global chkFlag

        #Checking if the file count has reached 50 (i.e 5GB)
        if (filecnt >= 50):
            print "filecnt"
            chkFlag = False
            return False

        #Checks the number of tweets
        if tweetcnt >= numTweets and numTweets != 0:
            print "first"
            chkFlag = False
            return False

        #Create a new text file every 100MB
        if (f.tell() >= 104857600):
            print "last"
            f.close()
            chkFlag= True
            filecnt += 1

            file_output_path = dirName+"/tweets_data{0}.txt".format(filecnt)
            f = open(file_output_path, 'a')

        
        decoded = json.loads(data)

        #Get Hastags
        hashTags = decoded['entities']['hashtags']
        if (hashTags != "[]"):
            for htags in hashTags:
                hashTags = unicode(htags['text']).encode("ascii","ignore")

        #Get tweet
        tweet = unicode(decoded['text']).encode("ascii","ignore").replace('\n', ' ').replace('\t', '').replace('\r', '')

        #Get Co-ordinates
        coord = unicode(decoded['coordinates']).encode("ascii","ignore")

        #Get tweet time
        tweetTime = unicode(decoded['created_at'])

        #Get retweet count
        retweetCount = unicode(decoded['retweet_count']).encode("ascii","ignore")

        #Get reply count
        replyCount = unicode(decoded['reply_count']).encode("ascii","ignore")

        #Get favorite count
        favoriteCount = unicode(decoded['favorite_count']).encode("ascii","ignore")

        #Get URLs
        urls = unicode(decoded['entities']['urls']).encode("ascii","ignore")

        #Get title
        pageTitle = None
        expanded_url = None
        if urls != "[]":
            expanded_url = unicode(decoded['entities']['urls'][0]['expanded_url']).encode("ascii","ignore")
            try:
                page = urllib2.urlopen(expanded_url)
                p = parse(page)
                pageT = p.find(".//title")
                if (pageT != None):
                    pageTitle = unicode(p.find(".//title").text).encode("ascii","ignore")

            except urllib2.HTTPError, err:
                if err.code == 404:
                    print "Page not found!"
                elif err.code == 403:
                    print "Access denied!"
                else:
                    print "Error:", err.code
            except urllib2.URLError, err:
                print "URL error:", err.reason
            except BadStatusLine:
                print "Could not fetch URL"

        if (pageTitle != None):
            pageTitle = re.sub('[^A-Za-z0-9]+', ' ', pageTitle)
            

        tweetData = "Hashtags:{0} Tweet:{1} Coordinates:{2} Date:{3} RetweetCount:{4} ReplyCount:{5} FavoriteCount:{6} URL:{7} Title:{8} ".format(
            hashTags, tweet, coord[36:-1], tweetTime, retweetCount, replyCount, favoriteCount, expanded_url, pageTitle)


        tweetcnt += 1
        print 'Tweet:', tweetcnt, ' F.size = ', f.tell(), ' on file:', filecnt
        tweetData += "\n"
        print tweetData
        f.write(tweetData)
        return True

    def on_error(self, status):
        print status
        if (status == 420):
            print "420 ERROR!!"
            return False


if __name__ == '__main__':

    while chkFlag != False:
        try:
            #Authentication and connection to twitter API
            auth = OAuthHandler(consumer_key, consumer_secret)
            auth.set_access_token(access_token, access_secret)
            stream = Stream(auth, twitterListener())

            stream.filter(track=["#ArtificalIntelligence", "#MachineLearning", "#BigData","#Google","#Microsoft","#IBM","#Saleforce","#lyft","#Uber","#AugmentedReality","#virtualReality","#MixedReality"
            ,"#Bots","#chatBots","#Blockchain","#cryptocurrencies","#javascript","#Node.js","#Angular.js","#React.js","#Walmart","#Quora","#DrivelessCars","#tesla","#twitter"
            ,"#facebook","#javascript","#cybersecurity","#icml2019","#NeuralNetworks" ,"#DeepLearning","#programming","#robotics" ,"#datascience","#3D","#Innovation","#ComputerVision"
            ,"#SiliconValley","#IoT","#techconferences","#jsconf","#dreamforce","#A2IC_Conference","#nodejsconfit" ,"#NodeDay","#AngularJSFan","#GoogleBrains","#IBMWatson","#io19"
            ,"#MSIgnite","#WWDC2018","#AWS"])

            Thread.map(stream)
        except Exception, e:
            print "Exception occured: "
            print e
            if (e == 420):
                waittime = 60;
                print "Waiting for " , waittime , " seconds..."
                time.sleep(waittime)
                print "Going"
            pass

    f.close()