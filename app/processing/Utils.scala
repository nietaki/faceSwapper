package processing
import play.api.Play
import play.api.Logger
import play.api.cache.Cache
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.codec.binary.Base64
import java.nio.file._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._
import scala.math._
import scaldi._


class Utils (implicit inj: Injector) extends Injectable {
  def readFile(relPath: String): Array[Byte] = {
    val file = Play.getFile(relPath)
    Files.readAllBytes(file.toPath())    
  }
  
  def base64Encode(bytes: Array[Byte]): String = {
    val bytes64 = Base64.encodeBase64(bytes)
    new String(bytes64)
  }
  //TODO: saveImageWithRandomName
  //TODO: retrieveRemoteImageBytes
  //TODO: binarytoMatByte?
  def toJavaByteList(collection: Array[Byte]): java.util.List[java.lang.Byte] = {
    ListBuffer(collection.map(x => Byte.box(x)): _*)
  }
  
  def loadImage(relPath: String, imdecodeFlags: Int): Mat = {
    val bytes = readFile(relPath)
    bytesToImage(bytes, imdecodeFlags)
  }
  
  def bytesToImage(bytes: Array[Byte], imdecodeFlags: Int): Mat = {
    assert(bytes.length > 100)
    var fileBytes = new MatOfByte()
    var encodedImageJavaList = toJavaByteList(bytes)
    fileBytes.fromList(encodedImageJavaList)
    Highgui.imdecode(fileBytes, imdecodeFlags)
  }

  def getProportion(image: Mat) = {
    image.width().toDouble / image.height().toDouble
  }
  
  def safeOverlay(background: Mat, position: Rect, image: Mat, mask: Mat)  = {
    val xover = max(position.br().x.toInt - background.width(), 0)
    val yover = max(position.br().y.toInt - background.height(), 0)
    
    val xunder = max(0 - position.tl().x.toInt, 0)
    val yunder = max(0 - position.tl().y.toInt, 0)
    if(xover == 0 && yover == 0 && xunder == 0 && yunder == 0)  {
      val bSubmat = background.submat(position)
      assert(image.size() == mask.size())
      assert(image.size() == bSubmat.size())
      image.copyTo(bSubmat, mask)
    } else {
      image.adjustROI(-yunder, -yover, -xunder, -xover)
      mask.adjustROI(-yunder, -yover, -xunder, -xover)
      val correctedPosition = position.clone()
      correctedPosition.width -= (xover + xunder)
      correctedPosition.height-= (yover + yunder)
      correctedPosition.x += xunder
      correctedPosition.y += yunder
      assert(image.size() == mask.size())
      assert(correctedPosition.size() == mask.size())
      
      val bSubmat = background.submat(correctedPosition)
      image.copyTo(bSubmat, mask)
    }
  }
  
  def trollify(baseImage: Mat, maskScaleMultiplier: Double) {
    val genericFaceDetector = inject [FaceDetector]
    //var baseImage = Utils.loadImage("public/images/dog_owner.jpg", Highgui.CV_LOAD_IMAGE_COLOR)
    var trollface = Cache.getOrElse[Mat]("images.trollface") {
      Logger.logger.info("preloading trollface")
      loadImage("res/Transparent_Troll_Face_ehnahced.jpg", Highgui.CV_LOAD_IMAGE_COLOR)
    }
    val mask = Cache.getOrElse[Mat]("images.mask") {
      loadImage("res/troll_cropped_mask.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE)
    }
    
    var trollProportion = getProportion(trollface)
    //assert(baseImage.channels() == trollface.channels())
    //assert(baseImage.depth() == trollface.depth())
    var faceDetections = genericFaceDetector.detectFaces(baseImage)

    var sortedFaces = faceDetections.toArray.sortBy(_.area())
    
    sortedFaces.toArray.foreach(rect => {
      val multiplier = maskScaleMultiplier * rect.height.toDouble / trollface.height.toDouble
      
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
      
      safeOverlay(baseImage, modrect, smallface, smallmask)
    }) 
  }
}