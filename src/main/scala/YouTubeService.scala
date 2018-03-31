import com.google.api.client.http.{HttpRequest, HttpRequestInitializer}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube

object YouTubeService {
  val youtube = new YouTube.Builder( new NetHttpTransport()
    , new JacksonFactory()
    , new HttpRequestInitializer(){
      override def initialize(request: HttpRequest): Unit = {

      }
    }
  ).setApplicationName("youtube_cmd").build

}
