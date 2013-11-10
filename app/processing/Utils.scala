package processing
import play.api._
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.codec.binary.Base64
import java.nio.file._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.opencv.core._
import org.opencv.highgui._
import scala.math._


object Utils {
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
  
  def loadImage(relPath: String, imdecodeFlags: Int) = {
    val image = Utils.readFile(relPath)
    var fileBytes = new MatOfByte()
    var encodedImageJavaList = Utils.toJavaByteList(image)
    fileBytes.fromList(encodedImageJavaList)
    Highgui.imdecode(fileBytes, imdecodeFlags);
  }
  
  def overlayImage(background: Mat, foreground: Mat, output: Mat, location: Point) = {
    background.copyTo(output);
    
    // start at the row indicated by location, or at row 0 if location.y is negative.
    for(y: Int <- (max(location.y.round.toInt , 0) to background.rows);
        fY: Int = y - location.y.round.toInt // because of the translation;
        if(fY < foreground.rows);// we are done of we have processed all rows of the foreground image.
        xPos: Int = max(location.x.round.toInt, 0);
        x <- (xPos to background.cols);
        fX: Int = x - location.x.round.toInt; // because of the translation.
        if(fX < foreground.cols)
        ) 
    {
      // determine the opacity of the foregrond pixel, using its fourth (alpha) channel.
      var foregroundPixel: Array[Double] = foreground.get(fY, fX);
      var opacity: Double = foregroundPixel(3);
      //TODO copyTo with a mask
      if(opacity > 0) {
        
        output.put(y, x, foregroundPixel.map(_.toFloat))
      }
    }
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
    //small.copyTo(bSubmat)
  }
}