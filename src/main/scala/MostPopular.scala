import pureconfig.loadConfigOrThrow
import com.google.api.services.youtube.model.VideoListResponse
import scala.collection.JavaConversions._
object MostPopular extends App {

  val config = loadConfigOrThrow[YouTubeConfig]
  val youtube = YouTubeService.youtube

  val list = youtube.videos.list("snippet,contentDetails,statistics")
  list.setKey(config.apiKey)
  list.setChart("mostPopular")
  list.setRegionCode("RU")
  list.setVideoCategoryId("")
  list.setMaxResults(config.maxResults)


  val response = list.execute
  response.getItems.toList.foreach{
    x =>
      val count = x.getStatistics.getViewCount
      val likeCount  = x.getStatistics.getDislikeCount
      val title  = x.getSnippet.getTitle
      println(count,likeCount,title)
  }


}
