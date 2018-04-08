import java.util
import java.util.List

import Search.config
import pureconfig.loadConfigOrThrow

import scala.collection.JavaConversions._
object Comments extends App {
  val config = loadConfigOrThrow[YouTubeConfig]
  val youtube = YouTubeService.youtube

  import com.google.api.services.youtube.model.CommentThread
  import com.google.api.services.youtube.model.CommentThreadListResponse
  var pageToken:String = null
  var comments:List[CommentThread] = new util.ArrayList[CommentThread]()
  for (_ <- 1 to 30 )  {
    val videoCommentsListResponse = youtube.commentThreads
      .list("snippet")
      .setKey(config.apiKey)
      .setVideoId("gyWklYCMv7E")
      .setTextFormat("plainText")
      .setPageToken(pageToken)
      .setMaxResults(100L)
      .execute

    pageToken = videoCommentsListResponse.getNextPageToken
  //  println(pageToken)
    val videoComments = videoCommentsListResponse.getItems
    comments.addAll(videoComments)
  }
  comments.toList.map{
    x =>
      val text = x.getSnippet().getTopLevelComment()
        .getSnippet().getTextDisplay;
      val parent = x.getSnippet().getTopLevelComment.getSnippet.getParentId
      x.getSnippet.getIsPublic
      val replyCount = x.getSnippet.getTotalReplyCount
      val likeCount = x.getSnippet.getTopLevelComment.getSnippet.getLikeCount
      (replyCount, likeCount, text)
  }.sortWith((a,b) => a._2 > b._2).foreach(println)
}
