package controllers


import play.api._
import play.api.Logger.logger
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.codec.binary.Base64
import java.io._
import java.nio.file._
import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._
import org.opencv.imgproc.Imgproc
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.opencv.objdetect.CascadeClassifier
import processing._
import scaldi.Injectable
import scaldi.Injector
import play.api.libs.ws.WS
import scala.concurrent.Future
import play.api.libs.ws.Response
import scala.util.Random

class Application(implicit inj: Injector) extends Controller with Injectable {
  
  val utils = inject [Utils] 
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def demo(filename: String) = Action {
    val baseImage = utils.loadImage("public/images/" + filename, Highgui.CV_LOAD_IMAGE_COLOR)
    
    utils.trollify(baseImage, 1.4) //TODO przesunąć maskę lekko w lewo
    
    val outputImage = new MatOfByte();
    Highgui.imencode(".png", baseImage, outputImage);
    val outputBytes = outputImage.toArray();

    Highgui.imwrite("highOutput.png", baseImage)
    //Ok(views.html.showImage(encodedImage))
    Ok(views.html.showImage(utils.base64Encode(outputBytes)))
  }
  
  def service(name: String) = Action.async {
    logger.info(name)
    val holder= WS.url(name)
    val complexHolder = holder  .withFollowRedirects(true)
                                 .withRequestTimeout(10000)
    val futureResponse: Future[Response] = complexHolder.get()
    futureResponse.map {response =>
        val bytes = response.ahcResponse.getResponseBodyAsBytes()
        val inputImage = utils.bytesToImage(bytes, Highgui.CV_LOAD_IMAGE_COLOR)
        utils.trollify(inputImage, 1.3)
        val outFilename = Random.alphanumeric.take(12).mkString
        Highgui.imwrite("results/" + outFilename + ".jpg", inputImage)
        MovedPermanently("/results/" + outFilename + ".jpg")
      }
    //val body = holder.get().value.get.get.body
    //Ok(views.html.index(name))
  }
  def serviceTest = Action {
    logger.info("serviceTest, redirecting")
    MovedPermanently("/assets/images/test.jpg")
  }
  
  def at(path: String, file: String) = Action {
    Ok(utils.readFile(path + "/"+ file)).as("image/jpeg")
  }
}