import java.util.{Properties, Scanner}

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.{HttpRequest, HttpRequestInitializer}
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.{SearchListResponse, SearchResult}
import com.typesafe.config.ConfigFactory
import pureconfig.error.ConfigReaderFailures
import pureconfig.error.ConfigReaderFailures
import pureconfig.loadConfig
import pureconfig.loadConfigOrThrow
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}
import com.typesafe.config.ConfigFactory
object Search extends App {

  val config = loadConfigOrThrow[YouTubeConfig]

  def readUserInputAndExecute(): Unit ={
    val scanner = new Scanner(System.in)
    while(scanner.hasNextLine)
      searchByString(scanner.nextLine)
  }
  def searchByString(searchString:String): Unit ={

    val youtube = new YouTube.Builder( new NetHttpTransport()
      , new JacksonFactory()
      , new HttpRequestInitializer(){
        override def initialize(request: HttpRequest): Unit = {

        }
      }
    ).setApplicationName("youtube_cmd").build

    val search = youtube.search().list("id")
    search.setKey(config.apiKey)
    search.setQ(searchString)
    search.setType("video")
    search.setFields("items(id/kind,id/videoId)")
    search.setMaxResults(config.maxResults)
    val searchResponse = search.execute
    val searchResult = searchResponse.getItems

    searchResult.toList.map(x=>getTitleAndViewCount(youtube,config.apiKey,x)).foreach(println)
  }

  def getTitleAndViewCount(yt: YouTube, key:String,v: SearchResult): (BigInt,String) ={
    val videoResponse = yt.videos().list("snippet,contentDetails,statistics").setId(v.getId.getVideoId).setKey(key).execute()
    val video =  videoResponse.getItems.get(0)
   (video.getStatistics.getViewCount,video.getSnippet.getTitle)

  }
  readUserInputAndExecute
}
