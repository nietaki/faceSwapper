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
    
    var baseImage = Utils.loadImage("public/images/la_hard.jpg", Highgui.CV_LOAD_IMAGE_COLOR)
    //var baseImage = Utils.loadImage("public/images/dog_owner.jpg", Highgui.CV_LOAD_IMAGE_COLOR)
    var trollface = Utils.loadImage("res/Transparent_Troll_Face_ehnahced.jpg", Highgui.CV_LOAD_IMAGE_COLOR)
    var mask = Utils.loadImage("res/troll_cropped_mask.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE)
    var small = Utils.loadImage("res/troll_small.jpg", Highgui.CV_LOAD_IMAGE_COLOR)
    
    var trollProportion = Utils.getProportion(trollface)
    assert(baseImage.channels() == trollface.channels())
    assert(baseImage.depth() == trollface.depth())
    var faceDetections = genericfaceDetector.detectFaces(baseImage)

    if(faceDetections.toArray.length == 0) {
      System.out.println("no detections!")
    }
    
    var sortedFaces = faceDetections.toArray.sortBy(_.area())
    
    sortedFaces.toArray.foreach(rect => {
      val multiplier = 1.5 * rect.height.toDouble / trollface.height.toDouble
      
      val midX: Int = ((rect.tl().x + rect.br().x)/2.0).toInt
      val midY: Int = ((rect.tl().y + rect.br().y)/2.0).toInt
      
      val width: Int = (multiplier * trollface.width).toInt
      val height = (multiplier * trollface.height).toInt 
      
      val tlX = midX - width/2
      val tlY = midY - height/2
      
      val smallface = new Mat()
      val smallmask = new Mat()
      val modrect = new Rect(tlX, tlY, width, height)
      //Imgproc.resize(trollface, smallface, rect.size() , 0, 0, Imgproc.INTER_LINEAR)
      Imgproc.resize(trollface, smallface, modrect.size() , 0, 0, Imgproc.INTER_LINEAR)
      Imgproc.resize(mask, smallmask, modrect.size() , 0, 0, Imgproc.INTER_LINEAR)
      //var bSubmat = baseImage.submat(10, small.rows() + 10, 10, small.cols() + 10)
      
      Utils.safeOverlay(baseImage, modrect, smallface, smallmask)
    })
    /*
    
    var smallface = new Mat();
    var otherRect: Rect = new Rect(10, 10, 150, 150)
    var target2 = baseImage.submat(otherRect)
    faceDetections.toArray.foreach( rect => {
      //Imgproc.resize(trollface,smallface, rect.size(), 0, 0, Imgproc.INTER_AREA);
      Imgproc.resize(trollface,smallface, new Size(2,2), 0, 0, Imgproc.INTER_AREA)
      var target = baseImage.submat(rect)
      //var target = new Mat(baseImage, rect)
      System.out.println(target)
      System.out.println(smallface)
      smallface.copyTo(target)
      otherRect = rect;
      Core.rectangle(baseImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0))
      Core.rectangle(target, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0))
      //Utils.overlayImage(baseImage, smallface, baseImage, new Point(30, 30))
    })
    */
    var outputImage = new MatOfByte();
    Highgui.imencode(".png", baseImage, outputImage);
    var outputBytes = outputImage.toArray();

    Highgui.imwrite("highOutput.png", baseImage)
    //Ok(views.html.showImage(encodedImage))
    Ok(views.html.showImage(Utils.base64Encode(outputBytes)))
  }
  


}