/* Author: Lokesh Koppaka, Abhilash Sunkam, Vishal Lella
 * Description : Main Class holds logic for user preferences and according calling TweetSeacher and TweetIndexer
 * 
 */
import java.io.IOException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import java.util.Scanner;
public class TwitterSearchEngine {
	// Constructor - instantiates necessary members
	public void createIndex(String directoryPath, String dataPath) {
		try {
			TweetIndexer ti = new TweetIndexer(directoryPath);
			ti.createIndex(dataPath, ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		// Logic for tweet index generation
		if(args.length == 3 && args[2].equals("1")) {
			String directoryPath = args[0]; //directory path where index file is generation
			String dataPath = args[1];// data file path
			TwitterSearchEngine tse = new TwitterSearchEngine();
			long startTime = System.nanoTime();
			tse.createIndex(directoryPath,dataPath);
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			System.out.println("Time Taken = " + timeElapsed/1000000 + "millSec");
		}
		// Logic for tweet search
		else if(args.length == 2 && args[1].equals("2")){
			System.out.println("******** Welcome Tech Tweet Seacher**********");
			String indexDirectory = args[0];//directory path where index file is presented
			Scanner sc = new Scanner(System.in); 
			System.out.println("Enter the search query = ");
			String query = sc.next();
			try {
				TweetSeacher ts = new TweetSeacher(indexDirectory);
				TopDocs topD = ts.search(query);
				System.out.println("------------------<== Query Top Results ==>--------------------");
				int rank = 1;
				for(ScoreDoc scrDoc : topD.scoreDocs) {
					System.out.println("Title = " + ts.getDocument(scrDoc).get("title"));
					System.out.println("HashTag = #" + ts.getDocument(scrDoc).get("hashTag"));
					System.out.println("Tweet = " + ts.getDocument(scrDoc).get("tweet"));
					//System.out.println("Coordinates = " + ts.getDocument(scrDoc).get("coordinates"));
					System.out.println("createdAt = " + ts.getDocument(scrDoc).get("createdAt"));
					//System.out.println("retweetCount = " + ts.getDocument(scrDoc).get("retweetCount"));
					//System.out.println("replyCount = " + ts.getDocument(scrDoc).get("replyCount"));
					System.out.println("URL = " + ts.getDocument(scrDoc).get("URL"));
					System.out.println("<== Score and Rank Info ==>");
					System.out.println("Rank = " + rank);
					System.out.println("Score = " + scrDoc.score);
					System.out.println("**********************************************************************");
					rank ++;
			}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
