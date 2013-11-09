package modules

import scaldi.Module
import processing._
import play.api.mvc.Controller

class UserModule extends Module {
  bind [FaceDetector] to new MultiScaleFaceDetector()
  bind [controllers.Application] to new controllers.Application()
}