package com.bytedance.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class DoubleClickClassVisitor(classVisitor: ClassVisitor): ClassVisitor(Opcodes.ASM8,classVisitor) {

    companion object {
        private const val METHOD_CLICK_NAME = "onClick"
        private const val VIEW_VOID = "(Landroid/view/View;)V"
    }

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions)
        return if (name == METHOD_CLICK_NAME && descriptor == VIEW_VOID) {
            FastClickMethodVisitor(api,methodVisitor,access,name,descriptor)
        } else {
            methodVisitor
        }

    }

    class FastClickMethodVisitor(api: Int,
                                 methodVisitor: MethodVisitor?,
                                 access: Int,
                                 name: String,
                                 descriptor: String): AdviceAdapter(api, methodVisitor, access, name, descriptor) {
        override fun onMethodEnter() {
            super.onMethodEnter()
            mv.visitMethodInsn(INVOKESTATIC,"com.bytedance.plugin.FirstClickUtils","isDoubleClickHappen","()Z",false)
            val label = Label()
            mv.visitJumpInsn(IFEQ,label)
            mv.visitInsn(RETURN)
            mv.visitLabel(label)
        }
    }
}