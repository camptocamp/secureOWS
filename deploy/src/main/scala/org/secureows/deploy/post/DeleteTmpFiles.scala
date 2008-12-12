/*
 * ImportData.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.secureows.deploy.post

import scalax.io.Implicits._
import Alias.toConverter

class DeleteTmpFiles extends Function1[Alias,Option[String]] {
    def apply(alias:Alias):Option[String]={
        assert (alias.tmpDir.asFile.deleteRecursively, "Unable to delete tmp directory for some reason: "+alias.tmpDir)
        None
    }

}
