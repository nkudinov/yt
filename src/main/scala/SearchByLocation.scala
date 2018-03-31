import Search.{config, getTitleAndViewCount}
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchResult
import pureconfig.loadConfigOrThrow

import scala.collection.JavaConversions._
object SearchByLocation extends App {
  val config = loadConfigOrThrow[YouTubeConfig]
  val youtube = YouTubeService.youtube
  val search = youtube.search().list("id")
  search.setKey(config.apiKey)
  search.setQ("школа")
  search.setType("video")
  search.setFields("items(id/kind,id/videoId)")
  search.setLocation("55.867139, 37.635466")
  search.setLocationRadius("0.5mi")
  search.setMaxResults(config.maxResults)
  val searchResult = search.execute.getItems
  searchResult.toList.map(x=>getTitleAndViewCount(youtube,config.apiKey,x)).foreach(println)

  def getTitleAndViewCount(yt: YouTube, key:String,v: SearchResult): (BigInt,String,String) ={
    val videoResponse = yt.videos().list("snippet,contentDetails,statistics").setId(v.getId.getVideoId).setKey(key).execute()
    val video =  videoResponse.getItems.get(0)

    (  video.getStatistics.getViewCount
      ,video.getSnippet.getTitle
      , video.getId
    )
  }
}
