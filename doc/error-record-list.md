#错误日志

##2016-01-12 18:48
##==================================================================================================
###情景描述
java.io.NotSerializableException
将Serializable改为Parcelable

##2016-01-04 15:32
##==================================================================================================
###情景描述
Nexus 6P识别屏幕分辨率为1440X2392，因为6.0系统的底部栏高度不被计入。

##2015-12-14 18:12
##==================================================================================================
###情景描述
I have two project A and B where B is added as a module of project A.
I have added dependencies in A's Gradle build file.
Now I can import B's class in A without any error (in editor) but can't build.
XXX is a class of project B.
###错误描述
Error: cannot find symbol class XXX
###解决方法
0.Copy same proguard conf on all build.gradle has solved the "cannot find symbol class" error.
1.My problem was related - under buildTypes, "minifyEnabled" was set to "true" in one module and "false" in the app module.
I set it to false in both, and now it works.
2.I have pointed out the problem. TargetSdk version and support package version of two project are not same.
After changing these with latest version, my problem is solved.
3.I had this error come up when I added a new module to my project.
To fix it, I also had to change minSdkVersion, targetSdkVersion, buildToolsVersion, and compileSdkVersion to match the build.gradle in my original module.
After I did these things the error was still coming up so I set minifyEnabled to false and then it compiled and ran!
###总结
It can happen if the library (be it a local module or external dependency) has a minifyEnabled true,
but library's ProGuard configuration is missing or not correct (the class is eligible for removing by ProGuard).
This leads to class not being compiled.
###参考链接
http://stackoverflow.com/questions/28147013/cannot-find-symbol-class-in-android-studio