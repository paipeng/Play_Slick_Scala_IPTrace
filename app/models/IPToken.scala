package models

import play.api.db.slick.Config.driver.simple._
import java.sql.Timestamp

case class IPToken(id: Long, getToken: String, setToken: String, createdAt: Timestamp)


/* Table mapping
 */
class IPTokensTable(tag: Tag) extends Table[IPToken](tag, "ip_tokens") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def getToken = column[String]("get_token", O.NotNull)
  def setToken = column[String]("set_token", O.NotNull)
  def createdAt = column[Timestamp]("created_at", O.NotNull, O.DBType("timestamp default now()"))
  
  def * = (id, getToken, setToken, createdAt) <> (IPToken.tupled, IPToken.unapply _)
  
  //val tokens: TableQuery[IPTokensTable] = TableQuery[IPTokensTable]
  
  //def findById(tokenId: Long): Option[IPToken] = filter { _.id === tokenId }.firstOption
  
}
