package processing
import play.api._
import play.api.mvc._
import play.api.Play.current
import org.apache.commons.codec.binary.Base64
import java.nio.file._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer


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
}