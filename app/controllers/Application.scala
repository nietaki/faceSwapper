package controllers


import play.api._
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.codec.binary.Base64
import java.io._
import java.nio.file._
import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc.Imgproc
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.opencv.objdetect.CascadeClassifier
import processing._
import scaldi.Injectable
import scaldi.Injector

class Application(implicit inj: Injector) extends Controller with Injectable {
  
  val genericfaceDetector = inject [FaceDetector]
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def showImage = Action {
    val image = Utils.readFile("public/images/dog_owner.jpg")
    
    val encodedImage = Utils.base64Encode(image)
    var imageMat = new MatOfByte()
    var encodedImageJavaList = Utils.toJavaByteList(image)
    imageMat.fromList(encodedImageJavaList)
    
    var decodedImage = Highgui.imdecode(imageMat, Highgui.CV_LOAD_IMAGE_COLOR);
    
    var faceDetections = genericfaceDetector.detectFaces(decodedImage)

    if(faceDetections.toArray.length == 0) {
      System.out.println("no detections!")
    }
    faceDetections.toArray.foreach( rect => {
      Core.rectangle(decodedImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0))
    })
    
    var outputImage = new MatOfByte();
    Highgui.imencode(".png", decodedImage, outputImage);
    var outputBytes = outputImage.toArray();

    Highgui.imwrite("highOutput.png", decodedImage)
    //Ok(views.html.showImage(encodedImage))
    Ok(views.html.showImage(Utils.base64Encode(outputBytes)))
  }
  


}