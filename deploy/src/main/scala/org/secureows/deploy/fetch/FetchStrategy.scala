package org.secureows.deploy.fetch
import org.secureows.deploy.Alias
/** 
 * Interface for obtaining the application(s) for deploying
 */
trait FetchStrategy {
  def downloadApp(alias:Alias)
  def finalConfiguration(alias:Alias)
}
