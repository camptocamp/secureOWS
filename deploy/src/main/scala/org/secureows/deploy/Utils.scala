package org.secureows.deploy

import scalax.io._
import Implicits._
import java.io.{File,InputStream,OutputStream,PrintStream}
import scala.actors.Actor
import scala.actors.Actor._

/**
 * Similar in behaviour to the java.lang.ProcessBuilder but provides some
 * conveniences that make it easier to use.
 * 
 * By default the output and error streams from the process will be written to stdout and to 
 * stderr.  You can customize the handling of the process output and error by passing a 
 * function to output() or error()
 * 
 * 
 * You can use input() to provide the input the process.  It is essentially piped in to the process without interaction.  If
 * you wish to create input based on the output of the process then it is recommended to asynchronously execute the process and 
 * carefully handle the interaction.
 * 
 * The apply methods and run() will synchronously execute the process
 * 
 */
class ProcessRunner(cOutput:((InputStreamResource[InputStream]))=>Unit,
                    cError:(InputStreamResource[InputStream])=>Unit,
                    cInput:Array[Byte],
                    val cEnv:Map[String,String],
                    cDir:File,
                    cLog:Boolean,
                    cCmds:String*){
                    
    def this(mCmds:String*)=this(ProcessRunner.pipe(System.out)_,
                                 ProcessRunner.pipe(System.err)_,
                                 new Array[Byte](0),
                                 ProcessRunner.defaultEnv,
                                 new File("."),
                                 false,
                                 mCmds:_*)
    def this(cOutput:((InputStreamResource[InputStream]))=>Unit,mCmds:String*)={
        this(cOutput,cOutput,new Array[Byte](0),ProcessRunner.defaultEnv,new File("."),false,mCmds:_*)
    }
                  
    private val cmdAsList=new scala.collection.jcl.ArrayList[String]()
    cmdAsList.addAll(cCmds)
    private val builder = new ProcessBuilder(cmdAsList.underlying).directory(cDir)
    scala.collection.jcl.Map(builder.environment())++=cEnv

    def run():Int={
        if(cLog) println("Executing Process: "+cCmds.mkString(" "))
        val process = builder.start()
        val mainActor = Actor.self
        val outActor = actor {
            try{
                cOutput(InputStreamResource(process.getInputStream))
            }finally{
                mainActor ! "done"
            }
        }
        val errActor = actor {
            try{
                cError(InputStreamResource(process.getErrorStream))
            }finally{
                mainActor ! "done"
            }
        }
        val in = process.getOutputStream

        try{
            in.write(cInput)
        }finally{
            in.flush()
        }
    
        val result = process.waitFor
        Actor.? // ack from one of the streams
        Actor.? // ack from the other stream
        result
    }

    def apply() = run
    def apply(pCmds:String*) = new ProcessRunner(cOutput,cError,cInput,cEnv,cDir,cLog,pCmds:_*).run
    
    def output(pHandler:(InputStreamResource[InputStream])=>Unit):ProcessRunner={
        new ProcessRunner(pHandler,cError,cInput,cEnv,cDir,cLog,cCmds:_*)
    }
    def error(pHandler:(InputStreamResource[InputStream])=>Unit):ProcessRunner={
        new ProcessRunner(cOutput,pHandler,cInput,cEnv,cDir,cLog,cCmds:_*)
    }

    def input(pIn:String):ProcessRunner={
        new ProcessRunner(cOutput,cError,pIn.getBytes,cEnv,cDir,cLog,cCmds:_*)
    }
  
    def log:ProcessRunner = {
        new ProcessRunner(cOutput,cError,cInput,cEnv,cDir,true,cCmds:_*)
    }
    def env(pEnv:Map[String,String]):ProcessRunner = {
        new ProcessRunner(cOutput,cError,cInput,pEnv,cDir,cLog,cCmds:_*)
    }
    def env(pEnv:(String,String)*):ProcessRunner = {
        new ProcessRunner(cOutput,cError,cInput,Map(pEnv:_*),cDir,cLog,cCmds:_*)
    }
    def dir(pWd:File):ProcessRunner = {
        new ProcessRunner(cOutput,cError,cInput,cEnv,pWd,cLog,cCmds:_*)
    }
    def dir(pWd:String):ProcessRunner = {
        new ProcessRunner(cOutput,cError,cInput,cEnv,new File(pWd),cLog,cCmds:_*)
    }
    /**
     * Execute a script in the local shell. If windows type is ignored and script is executed as a bat file.
     * <p> this functionality works by writing script to file and executing it. This way any script that can be executed on the local machine can be executed.
     * </p>
     * @param type. The path to the interpreter and is used in the #! Declaration. Ignored if on windows
     * @param script the script to execute
     */
    def script(scriptType:String, script:String)={
        val file=File.createTempFile(".script","",cDir)
        file.deleteOnExit()
        val complete="#!"+scriptType+"\n"+script
        file.write(complete)

        import DynamicObject._

        val windows = System.getProperty("os.name").contains("win")
    
        file ~> ("setExecutable",java.lang.Boolean.TRUE) match {
            case None if( !windows) => ProcessRunner("chmod", "+x", file.getAbsolutePath).run
            case _ =>
        }

        val result = if( windows) new ProcessRunner(cOutput, cError, cInput, cEnv, cDir, cLog, file.getAbsolutePath).run
        else new ProcessRunner(cOutput, cError, cInput, cEnv, cDir,cLog,file.getAbsolutePath).run

        if (!file.delete) file.deleteOnExit

        result
    }

}

object ProcessRunner{

    def apply(cmd:String*)=new ProcessRunner(cmd:_*)
    //def async(cmd:String*):RichProcess={null}
    def run(cmd:String*):Int=new ProcessRunner(cmd:_*)()
    def script(scriptType:String, script:String)= new ProcessRunner("").script(scriptType, script)

    private[deploy] def pipe(to:OutputStream)(from:InputStreamResource[InputStream])= {
        from.buffered.acquireFor{ stream =>
            var b = stream.read()
            while(b != -1){
                to.write(b)
                b = stream.read()
            }
        }
    }

    def defaultEnv:Map[String,String]={
        import scala.collection.jcl.Conversions._
        val env = convertMap(System.getenv)
        Map(env.map( e => e ).toSeq:_*)
    }
}

import java.lang.reflect._
/**
 * Provides a simple interface for invoking methods and setting fields on arbitrary objects.
 * <p>The companion object contains an implicit conversion from Any to Dynamic Object</p>
 */
class DynamicObject(target:AnyRef){
    /**
     * Invoke a method on the object.  Accessiblility are respected (private methods cannot be called).  
     * <p>This is an alias for invoke.</p>
     *
     * @param methodName the name of the method to invoke
     * @param args the arguments to pass to the method
     *
     * @return the result from the method or None if the method does not exist
     */
    def ~>(methodName:String):Option[Any] = invoke(methodName)
    
    /**
     * Invoke a method on the object.  Accessiblility are respected (private methods cannot be called).  
     * <p>This is an alias for invoke.</p>
     *
     * @param methodName the name of the method to invoke
     * @param args the arguments to pass to the method
     *
     * @return the result from the method or None if the method does not exist
     */
    def ~>(methodName:String,arg:AnyRef,args:AnyRef*)=invoke(methodName,arg,args:_*)
    
    /**
     * Invoke a method on the object.  Accessiblility are respected (private methods cannot be called).
     * @param methodName the name of the method to invoke
     * @param args the arguments to pass to the method
     *
     * @return the result from the method
     */
    def invoke[R](methodName:String):Option[R] = {
        val method = target.getClass.getDeclaredMethods().find( m =>{
                m.getName.equals(methodName) &&
                m.getParameterTypes.length == 0
            }
        ) 
        method match {
            case Some(m) => Some(m.invoke(target).asInstanceOf[R])
            case None => None
        }
    }
    
    /**
     * Invoke a method on the object.  Accessiblility are respected (private methods cannot be called).
     * @param methodName the name of the method to invoke
     * @param args the arguments to pass to the method
     *
     * @return the result from the method
     */
    def invoke[R](methodName:String,arg:AnyRef,args:AnyRef*):Option[R] = {
        val params = scala.Array(arg)++args
        val paramTypes = params.map( p => p.getClass )
        try{
            val method = target.getClass.getDeclaredMethod( methodName, paramTypes:_*)
            Some(method.invoke(target, params:_*).asInstanceOf[R])
        }catch{
            case _:NoSuchMethodException => None
        }
        
    }

}

object DynamicObject {
    implicit def anyToDynamicObject(target:AnyRef):DynamicObject = new DynamicObject(target)
}

object Utils {
    import DynamicObject._
    def setExecutable(file:File)={
        val windows = System.getProperty("os.name").contains("win")
    
        file ~> ("setExecutable",java.lang.Boolean.TRUE) match {
            case None if( !windows) => ProcessRunner("chmod", "+x", file.getAbsolutePath).run
            case _ =>
        }
    }
    def relative( root:File, file:File ):String = {
        file.getPath.drop(root.getPath.length+1)
    }
    def copyTree(from:File, to:File):Int = {
        copyTree(from,to, (s,d)=>s.lastModified>d.lastModified)
    }
    def replaceTree(from:File, to:File):Int = {
        copyTree(from,to, (s,d)=>true)
    }
    def copyTree(from:File, to:File, replace:(File,File)=>Boolean):Int = {
        val files = from.tree
        files.foldLeft(0)( (count,f) => {
                val dest = new File(to, relative(from,f))
                if(f.isDirectory) dest.mkdirs
                else if(!dest.exists || replace(f,dest) ) {
                    dest.delete()
                    f.copyTo(dest)
                }
      
                if(f.isFile && f.canExecute ) dest.setExecutable(true)
                count + 1
            })
    }
    def zipDir(source:File, to:File){
        import java.util.zip._
        val stream=OutputStreamResource(new ZipOutputStream(new java.io.FileOutputStream(to)))
        stream.acquireFor { out =>
            for( f <- source.tree){
                val name = relative(source, f) + (if( f.isFile ) "" else "/")
                val entry = new ZipEntry( name )
        
                out.putNextEntry(entry)
                if( f.isFile ) {
                    val bytes = f.inputStream.buffered.slurp
                    out.write( bytes, 0, bytes.length)
                }
                out.closeEntry()
            }
        }
    }
    def doCheckout(dir:File, url:String){
        assert (!dir.exists || dir.deleteRecursively, "unable to delete directory "+dir+" forced to abort")
        assert (dir.mkdirs, "unable to make directory "+dir+" forced to abort")
        ProcessRunner("svn","co",url,dir.getAbsolutePath).output( _.lines.toList).error( _.lines.toList).run
    }
}