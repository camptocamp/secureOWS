/*
 * RunOp.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.secureows.deploy

object RunOp {
    def run(args:Seq[String], config:Configuration):Option[String] = {
        (config.function(config.config(args(0))))(config.alias(args(1)))
    }
}
