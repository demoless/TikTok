package plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AsmTransform: Transform() {
    companion object {
        private const val TAG = "AsmTransform"
    }
    override fun getName(): String {
        return TAG
    }


    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        //需要处理的数据类型,这里表示class文件
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        //是否增量编译
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        //作用范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    private val mWaitableExecutor by lazy {
        WaitableExecutor.useGlobalSharedThreadPool().apply {
            waitForTasksWithQuickFail<Any>(true)
        }
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        val inputs: Collection<TransformInput>? = transformInvocation.inputs
        val outputProvider: TransformOutputProvider? = transformInvocation.outputProvider
        val isIncremental = transformInvocation.isIncremental

        if(!isIncremental) {
            //非增量编译 删除之前的所有文件
            outputProvider?.deleteAll()
        }
        println("inputs size: ${inputs?.size}")
        inputs?.forEach { input ->
            input.jarInputs.forEach { jarInput ->
                try {
                    //处理jar
                    mWaitableExecutor.execute {
                        processJarInput(jarInput, outputProvider!!, isIncremental)
                        return@execute null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            input.directoryInputs.forEach { directoryInput ->
                try {
                    //处理源码文件
                    mWaitableExecutor.execute {
                        processDirectoryInput(directoryInput, outputProvider!!, isIncremental)
                        return@execute null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mWaitableExecutor.waitForTasksWithQuickFail<Any>(true)
    }

    private fun processDirectoryInput(directoryInput: DirectoryInput, outputProvider: TransformOutputProvider,isIncremental: Boolean) {
        val directory = outputProvider.getContentLocation(directoryInput.name,directoryInput.contentTypes,directoryInput.scopes,Format.DIRECTORY)
        FileUtils.forceMkdir(directory)
        if (isIncremental) {
            val srcPath = directoryInput.file.absolutePath
            val destPath = directory.absolutePath
            val fileStatusMap = directoryInput.changedFiles
            fileStatusMap.entries.forEach { changeFile ->
                val status = changeFile.value
                val inputFile = changeFile.key
                val destFilePath = inputFile.absolutePath.replace(srcPath,destPath)
                val destFile = File(destFilePath)
                when (status) {
                    Status.NOTCHANGED -> return
                    Status.CHANGED, Status.ADDED -> {
                        FileUtils.touch(destFile)
                        transformSingleFile(inputFile,destFile)
                    }
                    Status.REMOVED -> {
                        if (destFile.exists()) {
                            FileUtils.forceDelete(destFile)
                        }
                    }
                }
            }
        } else {
            transformDirectory(directoryInput,directory)
        }
    }

    private fun processJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider,isIncremental: Boolean) {
        val dest = outputProvider.getContentLocation(jarInput.name,jarInput.contentTypes,jarInput.scopes,Format.JAR)
        if (isIncremental) {
            when(jarInput.status) {
                Status.NOTCHANGED -> {
                    return
                }

                Status.ADDED,Status.CHANGED -> {
                    FileUtils.copyFile(jarInput.file,dest)
                }

                Status.REMOVED -> {
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                }
            }
        } else {
            FileUtils.copyFile(jarInput.file,dest)
        }
    }

    private fun transformSingleFile(inputFile: File, destFile: File) {
        println("拷贝单个文件")
        traceFile(inputFile, destFile)
    }

    private fun traceFile(inputFile:File, outputFile:File) {
        if (isNeedTraceClass(inputFile)) {
            println("${inputFile.name} ---- 需要插桩 ----")
            val inputStream = FileInputStream(inputFile)
            val outputStream = FileOutputStream(outputFile)

            val classReader = ClassReader(inputStream)
            val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
            classReader.accept(getClassVisitor(classWriter), ClassReader.EXPAND_FRAMES)
            outputStream.write(classWriter.toByteArray())

            inputStream.close()
            outputStream.close()
        } else {
            FileUtils.copyFile(inputFile, outputFile)
        }
    }

    private fun isNeedTraceClass(inputFile: File):Boolean {
        val name = inputFile.name
        return !(!name.endsWith(".class")
                || name.startsWith("R.class")
                || name.startsWith("R$"))
    }

    private fun getClassVisitor(classWriter:ClassWriter): ClassVisitor {
        return DoubleClickClassVisitor(classWriter)
    }

    private fun transformDirectory(directoryInput: DirectoryInput,dest: File) {
        val extensions = arrayOf("class")
        //递归地去获取该文件夹下面所有的文件
        val fileList = FileUtils.listFiles(directoryInput.file,extensions,true)
        val outputFilePath = dest.absolutePath
        val inputFilePath = directoryInput.file.absolutePath
        fileList.forEach { inputFile ->
            println("替换前  file.absolutePath = ${inputFile.absolutePath}")
            val outputFullPath = inputFile.absolutePath.replace(inputFilePath, outputFilePath)
            println("替换后  file.absolutePath = ${outputFullPath}")
            val outputFile = File(outputFullPath)
            //创建文件
            FileUtils.touch(outputFile)
            //单个单个地复制文件
            transformSingleFile(inputFile, outputFile)
        }
    }
}