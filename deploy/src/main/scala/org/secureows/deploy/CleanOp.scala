/*
 * CleanOp.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.secureows.deploy

import scalax.io.Implicits._
import org.secureows.deploy.Alias.toConverter

object CleanOp {
    def run(args:Seq[String],config:Configuration):Option[String]={
        assert(args.length==1, "There must be exactly one alias for clean to run")
        val alias = config.alias(args(0))
        InstallOp.stopServer(alias)
        
        assert( alias.tmpDir.asFile.deleteRecursively, "unable to delete "+alias.tmpDir )
        for ( app <- alias.webapps;
              dir = alias.installWebappBaseDir+app) {
                    assert (dir.asFile.deleteRecursively, "unable to delete "+dir)
                    assert ((dir+".jar").asFile.deleteRecursively, "unable to delete "+dir+".jar")
              }

        InstallOp.startServer(alias)
        None
    }
}
