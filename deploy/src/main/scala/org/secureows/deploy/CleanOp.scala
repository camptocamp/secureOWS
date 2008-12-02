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
        val alias = config.alias(args(0))
        InstallOp.stopServer(alias)
        
        assert( alias.tmpDir.asFile.deleteRecursively, "unable to delete "+alias.tmpDir )
        for ( app <- alias.webapps;
              dir = alias.installWebappBaseDir+app) yield
                    assert (dir.asFile.deleteRecursively, "unable to delete "+dir)

        InstallOp.startServer(alias)
        None
    }
}
