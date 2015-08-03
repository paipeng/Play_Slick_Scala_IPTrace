package models

import play.api.db.slick.Config.driver.simple._
import java.sql.Timestamp

case class IPAddress(id: Long, tokenId: Long, address: String, createdAt: Timestamp)


/* Table mapping
 */
class IPAddresssTable(tag: Tag) extends Table[IPAddress](tag, "ip_addresses") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def tokenId = column[Long]("token_id", O.NotNull)
  def address = column[String]("address", O.NotNull)
  def createdAt = column[Timestamp]("created_at", O.NotNull, O.DBType("timestamp default now()"))
  
  def ipTokenKey = foreignKey("fk_geo_location", tokenId, TableQuery[IPTokensTable])(_.id)
  

  def * = (id, tokenId, address, createdAt) <> (IPAddress.tupled, IPAddress.unapply _)

}
