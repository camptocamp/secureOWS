package org.secureows.deploy.validation

import java.io.{File,PrintStream}

abstract class Result{val msg:String;val id:Int} 
case class Error(msg:String) extends Result{val id=0}
case class Warning(msg:String) extends Result{val id=1}
object Good extends Result{val msg="good";val id=2}

trait Validator {
  var log:PrintStream = System.out

  def validate(dir:File):Seq[Result]
  def validFor(dir:File):Boolean
}
