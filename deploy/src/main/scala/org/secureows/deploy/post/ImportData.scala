/*
 * ImportData.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.secureows.deploy.post

import java.io._
import java.net._
import scalax.io._

class ImportData extends Function1[Alias,Option[String]] {
    def apply(alias:Alias):Option[String]={
        Thread.sleep(10000)

        println("Importing and indexing test data")
        
        val login =  <request>
            <password>admin</password>
            <username>admin</username>
                     </request>

        val index = <request>
            <dir>{alias("testdata")}</dir>
            <category>_none_</category>
            <schema>iso19139.che</schema>
            <group>2</group>
            <styleSheet>GM03-to-ISO19139CHE.xsl</styleSheet>
                    </request>


        val cookie = request("http://"+alias.url+":8080/geonetwork/srv/eng/user.login", login, null)
        request("http://"+alias.url+":8080/geonetwork/srv/eng/util.import", index, cookie)

        None
    }

    def request(address:String, xml:scala.xml.Elem, cookie:String) = {
        println("Making request: "+address)
        val url = new URL(address)
        val conn = url.openConnection()
        if( cookie!=null ){
            conn.setRequestProperty("Cookie",cookie)
        }
        conn.setDoOutput(true)
        conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8")
        val out = OutputStreamResource(conn.getOutputStream)
        out.writeLine(xml.toString)

        val in = InputStreamResource(conn.getInputStream())
        in.lines.mkString("\n")
        conn.getHeaderField("Set-Cookie")
    }
}
