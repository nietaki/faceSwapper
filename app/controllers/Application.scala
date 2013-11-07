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

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def showImage = Action {
    val image = readFile("public/images/dog_owner.jpg")
    val encodedImage = base64Encode(image)
    var imageMat = new MatOfByte()
    var encodedImageJavaList: java.util.List[java.lang.Byte] = ListBuffer(image.map(x => Byte.box(x)): _*)
    imageMat.fromList(encodedImageJavaList)
    
    var decodedImage = Highgui.imdecode(imageMat, Highgui.CV_LOAD_IMAGE_COLOR);
    
    var faceDetections = new MatOfRect();
    var faceDetector = new CascadeClassifier(Play.getFile("res/lbpcascade_frontalface.xml").getPath());
    faceDetector.detectMultiScale(decodedImage, faceDetections)

    if(faceDetections.toArray.length == 0) {
      System.out.println("no detections!")
    }
    faceDetections.toArray.foreach( rect => {
      Core.rectangle(decodedImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0))
    })

    Highgui.imwrite("highOutput.png", decodedImage)
    Ok(views.html.showImage(encodedImage))
  }
  
  def readFile(relPath: String): Array[Byte] = {
    val file = Play.getFile(relPath)
    Files.readAllBytes(file.toPath())    
  }
  
  def base64Encode(bytes: Array[Byte]): String = {
    val bytes64 = Base64.encodeBase64(bytes)
    new String(bytes64)
    
  }

}