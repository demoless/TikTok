package plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class HelloWorldClassVisitor(private val classWriter: ClassWriter): ClassVisitor(Opcodes.ASM8, classWriter){
    override fun visitMethod(
        access: Int,
        name: String,
        descriptor: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = classWriter.visitMethod(access, name, descriptor, signature, exceptions)
        return HelloWorldMethodVisitor(api,methodVisitor,access, name, descriptor)
    }

    class HelloWorldMethodVisitor(api: Int,
                                  methodVisitor: MethodVisitor,
                                  access: Int, name: String,
                                  descriptor: String)
        : AdviceAdapter(api, methodVisitor, access, name, descriptor) {

        //扫描进入方法时执行
        override fun onMethodEnter() {
            println("-----------------HelloWorldClassVisitor onMethodEnter----------------")
/*            mv.visitFieldInsn(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;")
            mv.visitLdcInsn("Hello world!")
            mv.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V",false)*/
            mv.visitLdcInsn("log")
            mv.visitLdcInsn("hello world!")
            mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "d", "(Ljava/lang/String;Ljava/lang/String;)I", false)
        }

    }
}