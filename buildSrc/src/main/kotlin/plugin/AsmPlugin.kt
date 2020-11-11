package plugin

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmPlugin: Plugin<Project>{
    override fun apply(project: Project) {
        //注册方式1
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(getCustomTransform(project))
        //注册之后会在TransformManager#addTransform中生成一个task.

        //注册方式2
        //project.android.registerTransform(getCustomTransform())
    }

    private fun getCustomTransform(project: Project): Transform {
        return AsmTransform()
    }

}