import play.GlobalSettings;
import play.Application;
import org.opencv.core.Core;

class Global extends GlobalSettings {
  override def beforeStart(app: Application) {
    // TODO Auto-generated method stub
    //super.beforeStart(app);
    super.beforeStart(app)
    //System.out.println(System.getProperty("java.library.path")); ///usr/java/packages/lib/amd64:/usr/lib/jni:/lib:/usr/lib
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
    //System.loadLibrary("libopencv_java246") //
    //String libopencv_java = "/Users/yoonjechoi/git/myFirstApp/target/native_libraries/64bits/libopencv_java246.jnilib";
    //System.load(libopencv_java);
  }
}