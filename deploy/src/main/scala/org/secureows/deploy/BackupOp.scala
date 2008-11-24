package org.secureows.deploy
import java.io.File
import scalax.io.Implicits._

object BackupOp {
  
  val format = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss")
  
  def run(args:Seq[String],config:Configuration):Option[String]={
    val alias = config.alias(args(0))
    if( alias.maxBackups > 0 ){
      val newBackupDir = createBaseBackupDir(alias)
      for( app <- alias.webapps ){
        val toBackup=new File(alias.installWebappBaseDir + app)
        if(toBackup.exists) Utils.replaceTree(toBackup,newBackupDir)
      }
    }
    None
  }
    
  def createBaseBackupDir(alias:Alias) = {
    val backupRoot = new File(alias.backupDir)
    if(!backupRoot.exists){
      assert( backupRoot.mkdirs, "Unable to create "+backupRoot+" aborting!!!")
    }
    
    val backups=backupRoot.listFiles
    if(backups.size == alias.maxBackups){
      val sorted = backups.toList.sort((left,right)=> {
        left.lastModified>right.lastModified
      })
      sorted.head.deleteRecursively
    }
    val newBackupDir = backupRoot / System.currentTimeMillis.toString
    assert( newBackupDir.mkdirs, "Unable to create "+ newBackupDir +" aborting!!!")
    newBackupDir
  }
  
}
