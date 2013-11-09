package controllers


import play.api._
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

class Application(implicit inj: Injector) extends Controller with Injectable {
  
  val genericfaceDetector = inject [FaceDetector]
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def showImage = Action {
    
    var baseImage = Utils.loadImage("public/images/dog_owner.jpg", Highgui.CV_LOAD_IMAGE_UNCHANGED)
    var trollFace = Utils.loadImage("res/Transparent_Troll_Face_cropped.png", Highgui.CV_LOAD_IMAGE_UNCHANGED)
    
    var faceDetections = genericfaceDetector.detectFaces(baseImage)

    if(faceDetections.toArray.length == 0) {
      System.out.println("no detections!")
    }
    var finalImage = baseImage.clone();
    
    faceDetections.toArray.foreach( rect => {
      var smallface = new Mat();
      //Imgproc.resize(trollFace,smallface, rect.size(), 0, 0, Imgproc.INTER_AREA);
      Imgproc.resize(trollFace,smallface, new Size(10, 10), 0, 0, Imgproc.INTER_AREA);
      Core.rectangle(baseImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0))
      Utils.overlayImage(baseImage, smallface, baseImage, new Point(30, 30))
    })
    
    var outputImage = new MatOfByte();
    Highgui.imencode(".png", baseImage, outputImage);
    var outputBytes = outputImage.toArray();

    Highgui.imwrite("highOutput.png", baseImage)
    //Ok(views.html.showImage(encodedImage))
    Ok(views.html.showImage(Utils.base64Encode(outputBytes)))
  }
  


}