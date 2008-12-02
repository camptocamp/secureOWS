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
import scala.xml._
import net.lag.logging.Logger

class ImportData extends Function1[Alias,Option[String]] {
    def apply(alias:Alias):Option[String]={
        Thread.sleep(10000)

        println("Importing and indexing test data")
        
        val login =  <request>
            <password>admin</password>
            <username>admin</username>
                     </request>

        val index = <request>
            <dir>{alias("ImportData.testdata")}</dir>
            <category>_none_</category>
            <schema>iso19139.che</schema>
            <group>2</group>
            <styleSheet>GM03-to-ISO19139CHE.xsl</styleSheet>
                    </request>

        val port = alias.findOrElse("ImportData.port", "8080")
        val root ="http://"+alias.url+":"+port+"/geonetwork/srv/eng/"
        val cookie = request(root+"user.login", login, null, alias.logger)
        request(root+"util.import", index, cookie, alias.logger)

        setPriveleges(root, cookie, alias)
        None
    }

    def setPriveleges(rootURL:String, cookie:String, alias:Alias){

        println("Making all metadata public")

        val root = request(rootURL+"xml.search",None,cookie, alias.logger, (conn) =>{
                XML.load(conn.getInputStream)
            })

        val results = root \\ "metadata"
        for{ metadata <- results
            id = metadata \\ "id"
        }{
            val xml = <request>
                {id}
                <_0_6>on</_0_6>
                <_0_5>on</_0_5>
                <_1_6>on</_1_6>
                <_0_1>on</_0_1>
                <_1_5>on</_1_5>
                <_0_0>on</_0_0>
                <_1_1>on</_1_1>
                <_2_5>on</_2_5>
                <_1_0>on</_1_0>
                <_2_3>on</_2_3>
                <_2_1>on</_2_1>
                <_2_0>on</_2_0>
                      </request>

            val address = rootURL+"/metadata.admin"

            request(address,xml,cookie, alias.logger)

        }
    }

    def request(address:String, xml:scala.xml.Elem, cookie:String,logger:Logger) = {
        request[String](address,Some(xml), cookie, logger, (conn:java.net.URLConnection) => {
                val in = InputStreamResource(conn.getInputStream())
                in.lines.mkString("\n")
                conn.getHeaderField("Set-Cookie")
            })

    }

    def request[T](address:String, xml:Option[scala.xml.Elem], cookie:String, logger:Logger, run:(java.net.URLConnection) => T) = {
        println("request to "+address)
        logger.debug("Making URL request: %s with cookie: %s and XML: \n%s", address, cookie,xml)
        val url = new URL(address)
        val conn = url.openConnection()
        if( cookie!=null ){
            conn.setRequestProperty("Cookie",cookie)
        }

        if( xml.isDefined ){
            conn.setDoOutput(true)
            conn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8")
            val out = OutputStreamResource(conn.getOutputStream)
            out.writeLine(xml.get.toString)
        }
        run(conn)
    }

}
