package plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class AsmClassVisitor(cv: ClassVisitor): ClassVisitor(Opcodes.ASM8, cv) { //这里需要指定一下版本Opcodes.ASM8

    override fun visitMethod(access: Int, name: String, discripter: String, signature: String, exceptions: Array<out String>): MethodVisitor {
        return super.visitMethod(access, name, discripter, signature, exceptions)

    }

}