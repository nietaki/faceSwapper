package processing

import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfRect
import org.opencv.objdetect.CascadeClassifier
import play.api.Play.current
import play.api._

class MultiScaleFaceDetector extends FaceDetector {
  val classifier = new CascadeClassifier(Play.getFile("res/lbpcascade_frontalface.xml").getPath());
  
  def detectFaces(image: Mat): MatOfRect = {
    val ret = new MatOfRect;
    classifier.detectMultiScale(image, ret)
    ret
  }
}