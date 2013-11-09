package processing

import org.opencv.core._
import org.opencv.highgui._

trait FaceDetector {
  def detectFaces(image: Mat): MatOfRect
}