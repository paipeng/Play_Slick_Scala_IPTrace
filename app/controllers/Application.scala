package controllers

import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import java.sql.Timestamp

import play.api.libs.functional.syntax._
import play.api.libs.json._


object Application extends Controller{

  //create an instance of the table
  val cats = TableQuery[CatsTable] //see a way to architect your app in the computers-database-slick sample  

  var iptokens = TableQuery[IPTokensTable]
  var ipaddresses = TableQuery[IPAddresssTable]
  
  //JSON read/write macro
  implicit val catFormat = Json.format[Cat]

  implicit val rds: Reads[Timestamp] = (__ \ "time").read[Long].map{ long => new Timestamp(long) }
  implicit val wrs: Writes[Timestamp] = (__ \ "time").write[Long].contramap{ (a: Timestamp) => a.getTime }
  implicit val fmt: Format[Timestamp] = Format(rds, wrs)

  implicit val ipTokenFormat = Json.format[IPToken]
  implicit val ipAddressFormat = Json.format[IPAddress]

  def index = DBAction { implicit rs =>
    Ok(views.html.index(cats.list))
  }

  val catForm = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Cat.apply)(Cat.unapply)
  )

  def insert = DBAction { implicit rs =>
    val cat = catForm.bindFromRequest.get
    cats.insert(cat)

    Redirect(routes.Application.index)
  }

  def jsonFindAll = DBAction { implicit rs =>
    Ok(toJson(cats.list))
  }

  def jsonInsert = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[Cat].map { cat =>
        cats.insert(cat)
        Ok(toJson(cat))
    }.getOrElse(BadRequest("invalid json"))    
  }
  
  def echo(echo: String) = Action {
      Ok(echo);
  }
  
  def echoIP = Action { request =>
     val address = request.remoteAddress
    Ok(address)
  }
  
  def setIP(token: String) = DBAction { implicit request =>
      val address = request.remoteAddress
      val ipToken = iptokens.filter(_.setToken === token).first
      val t = new java.util.Date();
      val ipAddress = new IPAddress(-1, ipToken.id, address, new Timestamp(t.getTime()))
        ipaddresses.insert(ipAddress)
    //if (ipToken != null) {
    //    val ipAddresses = ipaddresses.filter(_.tokenId === ipToken.id)
    //}
    
    val newId = (ipaddresses returning ipaddresses.map(_.id) += ipAddress)
      //ipToken.id = autoGenratedKey
      Ok(toJson(ipAddress))
  }
  
  def getIPs(token: String) = DBAction { implicit rs =>
    val ipToken = iptokens.filter(_.getToken === token).first
    val ip = ipaddresses.filter(_.tokenId === ipToken.id)
    Ok(toJson(ip.list))
  }
  
  def getLastIP(token: String) = DBAction { implicit rs =>
    val ipToken = iptokens.filter(_.getToken === token).first
    val ip = ipaddresses.filter(_.tokenId === ipToken.id).sortBy(_.createdAt.desc).first
    Ok(toJson(ip))
  }
  
  def ipTokenFindAll = DBAction {
      implicit rs => Ok(toJson(iptokens.list))
  }
  
  def ipAddressesFindAll = DBAction {
      implicit rs => Ok(toJson(ipaddresses.list))
  }
  
  def generateIPToken(set: String, get: String) = DBAction { implicit rs =>
      val ipToken = new IPToken(-1, set, get, null)
      //val autoGenratedKey = iptokens.insert(ipToken)
      val newId = (iptokens returning iptokens.map(_.id) += ipToken)
      val ipToken2 = iptokens.filter(_.id === newId).first
      //ipToken.id = autoGenratedKey
      Ok(toJson(ipToken2))
      //implicit rs => Ok(toJson(ipToken))
      //Redirect(routes.Application.index)
  }
}
