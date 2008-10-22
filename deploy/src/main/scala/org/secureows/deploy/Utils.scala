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
                    cCmds:String*){
                    
  def this(mCmds:String*)=this(ProcessRunner.pipe(System.out)_,
                             ProcessRunner.pipe(System.err)_,
                             new Array[Byte](0), 
                             Map[String,String](),
                             new File("."),
                             mCmds:_*)
  def this(cOutput:((InputStreamResource[InputStream]))=>Unit,mCmds:String*)={
    this(cOutput,cOutput,new Array[Byte](0),Map[String,String](),new File("."),mCmds:_*)
  }
                  
  private val cmdAsList=new scala.collection.jcl.ArrayList[String]()
  cmdAsList.addAll(cCmds)
  private val builder = new ProcessBuilder(cmdAsList.underlying).directory(cDir)
  scala.collection.jcl.Map(builder.environment())++=cEnv

  def run():Int={
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
  def apply(pCmds:String*) = new ProcessRunner(cOutput,cError,cInput,cEnv,cDir,pCmds:_*).run
    
  def output(pHandler:(InputStreamResource[InputStream])=>Unit):ProcessRunner={
    new ProcessRunner(pHandler,cError,cInput,cEnv,cDir,cCmds:_*)
  }
  def error(pHandler:(InputStreamResource[InputStream])=>Unit):ProcessRunner={
        new ProcessRunner(cOutput,pHandler,cInput,cEnv,cDir,cCmds:_*)
  }

  def input(pIn:String):ProcessRunner={
        new ProcessRunner(cOutput,cError,pIn.getBytes,cEnv,cDir,cCmds:_*)
  }  
    
  def env(pEnv:Map[String,String]):ProcessRunner = {
    new ProcessRunner(cOutput,cError,cInput,pEnv,cDir,cCmds:_*)
  }
  def env(pEnv:(String,String)*):ProcessRunner = {
    new ProcessRunner(cOutput,cError,cInput,Map(pEnv:_*),cDir,cCmds:_*)
  }
  def dir(pWd:File):ProcessRunner = {
    new ProcessRunner(cOutput,cError,cInput,cEnv,pWd,cCmds:_*)
  }
  def dir(pWd:String):ProcessRunner = {
    new ProcessRunner(cOutput,cError,cInput,cEnv,new File(pWd),cCmds:_*)
  }
  /**
   * Execute a script in the local shell. If windows type is ignored and script is executed as a bat file.
   * <p> this functionality works by writing script to file and executing it. This way any script that can be executed on the local machine can be executed.
   * </p>
   * @param type. The path to the interpreter and is used in the #! Declaration. Ignored if on windows
   * @param script the script to execute
   */
  def script(scriptType:String, script:String)={
    val file=new File(cDir,".script"+System.currentTimeMillis)
    file.deleteOnExit()
    val complete="#!"+scriptType+"\n"+script
    file.write(complete)

    import DynamicObject._

    val windows = System.getProperty("os.name").contains("win")
    
    file ~> ("setExecutable",java.lang.Boolean.TRUE) match {
      case None if( !windows) => ProcessRunner("chmod", "+x", file.getName).run
      case _ => 
    }

    val result = if( windows) new ProcessRunner(cOutput, cError, cInput, cEnv, cDir, file.getName).run
                 else new ProcessRunner(cOutput, cError, cInput, cEnv, cDir,"./"+file.getName).run

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
          case Some(m) => Some(m.invoke(target, scala.Array[Object]()).asInstanceOf[R])
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
        val method = target.getClass.getDeclaredMethod( methodName, paramTypes.toArray) 
        Some(method.invoke(target, params).asInstanceOf[R])
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
	      case None if( !windows) => ProcessRunner("chmod", "+x", file.getName).run
	      case _ => 
	    }
    }
  	def relative( root:File, file:File ):String = {
		file.getPath.drop(root.getPath.length)
	}
	
	def copyTree(from:File, to:File):Int = {
		from.tree.projection.foldLeft(0)( (count,f) => {
			val dest = new File(to, relative(from,f))
			if(f.isDirectory) dest.mkdirs
			else if(!dest.exists || f.lastModified() > dest.lastModified() ) f.copyTo(dest)
			
			if( (f ~> "canExecute").getOrElse(false).asInstanceOf[Boolean] ) setExecutable(dest)
			count + 1
		})
	}
    def zipDir(source:File, to:File){
    import java.util.zip._
	val stream=OutputStreamResource(new ZipOutputStream(new java.io.FileOutputStream(to)))
	stream.acquireFor { out =>
		for( f <- source.tree; if f.isFile ){
			val entry = new ZipEntry( relative(source,f) )
			
			out.putNextEntry(entry)
			if( f.isFile ) {
				val bytes = f.inputStream.buffered.slurp
				out.write( bytes, 0, bytes.length)
			}
			out.closeEntry()
		}
	}
}
}