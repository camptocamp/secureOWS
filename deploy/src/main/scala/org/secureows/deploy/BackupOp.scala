package org.secureows.deploy
import java.io.File
import scalax.io.Implicits._

object BackupOp {
  
  val format = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
  
  def run(args:Seq[String],config:Configuration):Option[String]={
    val alias = args(0)
    val toBackup=new File(config.installWebapp(alias))
    if( config.maxBackups > 0 && toBackup.exists){
	    val backupRoot = new File(config.backupDir(alias))
	    if(!backupRoot.exists){
	       assert( backupRoot.mkdirs, "Unable to create "+backupRoot+" aborting!!!")
	    }
     
	    val backups=backupRoot.listFiles
	    if(backups.size == config.maxBackups){
	      val sorted = backups.toList.sort((left,right)=> {
	        left.lastModified>right.lastModified
	      })
	      sorted.head.deleteRecursively
	    }
	    val newBackupDir = backupRoot / System.currentTimeMillis.toString
	    
	     assert( newBackupDir.mkdirs, "Unable to create "+ newBackupDir +" aborting!!!")
         Utils.replaceTree(toBackup,newBackupDir)
    }
    None
  }
    
  
}
