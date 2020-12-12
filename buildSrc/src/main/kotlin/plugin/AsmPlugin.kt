package plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class AsmPlugin: AsmTransform(),Plugin<Project>{
    override fun apply(project: Project) {
        //注册方式1
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(this)
        //注册之后会在TransformManager#addTransform中生成一个task.

        //注册方式2
        //project.android.registerTransform(getCustomTransform())
    }

    override fun getClassVisitor(classWriter: ClassWriter): ClassVisitor {
        return HelloWorldClassVisitor(classWriter)
    }

}