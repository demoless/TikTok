package plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

class AsmPlugin: AsmTransform(),Plugin<Project>{
    override fun apply(target: Project) {
        //register transform
        val extension: BaseExtension= target.extensions.getByType(BaseExtension::class.java)
        if(extension is LibraryExtension) {
            isLibraryModule = true
        }
        extension.registerTransform(this)
    }

    override fun getName(): String {
        return "HotfixPlugin"
    }

    override fun getClassVisitor(classWriter: ClassWriter): ClassVisitor {
        return HelloWorldClassVisitor(classWriter)
    }

}