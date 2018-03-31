import java.util.Properties

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.{HttpRequest, HttpRequestInitializer}
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.{SearchListResponse, SearchResult}

import scala.util.{Failure, Success, Try}

object Search extends App {
  val PROPERTIES_FILENAME = "youtube.properties"

  val NUMBER_OF_VIDEOS_RETURNED:Long = 17

  val properties = new Properties()
  def init(): Unit ={

  }
  Try(properties.load(Search.getClass.getClassLoader.getResourceAsStream(PROPERTIES_FILENAME))) match {
    case Failure(e) => throw new RuntimeException(s"Can not load $PROPERTIES_FILENAME because of $e")
    case _ =>
  }

  val youtube = new YouTube.Builder( new NetHttpTransport()
                                   , new JacksonFactory()
                                   , new HttpRequestInitializer(){
                override def initialize(request: HttpRequest): Unit = {

                }
                }
  ).setApplicationName("My Project").build

  val search = youtube.search().list("id")
  val apiKey= properties.getProperty("youtube.apikey")
  search.setKey(apiKey)
  search.setQ("москва весна 2018")
  search.setType("video")
  search.setFields("items(id/kind,id/videoId)")
  search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED)



  val searchResponse = search.execute
  val searchResult = searchResponse.getItems
  import scala.collection.JavaConversions._
  searchResult.toList.map(x=>getTitleAndViewCount(youtube,apiKey,x)).foreach(println)

  def getTitleAndViewCount(yt: YouTube, key:String,v: SearchResult): (BigInt,String) ={
    val videoResponse = yt.videos().list("snippet,contentDetails,statistics").setId(v.getId.getVideoId).setKey(key).execute()
    val video =  videoResponse.getItems.get(0)
   (video.getStatistics.getViewCount,video.getSnippet.getTitle)

  }
}
