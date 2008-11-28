/*
 * RunOp.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.secureows.deploy

object RunOp {
    def run(args:Seq[String], config:Configuration):Option[String] = {
        (config.function(args(0)).get)(config.alias(args(1)))
    }
}
