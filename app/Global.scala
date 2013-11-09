import org.opencv.core.Core

import modules.UserModule
import play.api.Application
import play.api.GlobalSettings
import scaldi.play.ScaldiSupport

object Global extends GlobalSettings with ScaldiSupport {
  def applicationModule = new UserModule
  
  //override def getControllerInstance[A](controllerClass: Class[A]) = {
  //  super[ScaldiSupport].getControllerInstance(controllerClass)
  //}
  //override def onStart(app: Application) = {
  //  super[ScaldiSupport].onStart(app)
  //}
  override def beforeStart(app: Application) {
    //super.beforeStart(app);
    super.beforeStart(app)
    //System.out.println(System.getProperty("java.library.path")); ///usr/java/packages/lib/amd64:/usr/lib/jni:/lib:/usr/lib
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    //System.loadLibrary("libopencv_java246") //
    //String libopencv_java = "/Users/yoonjechoi/git/myFirstApp/target/native_libraries/64bits/libopencv_java246.jnilib";
    //System.load(libopencv_java);
  }
}