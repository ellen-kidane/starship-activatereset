package com.rallyhealth.starship-activatereset.models

import com.rallyhealth.spartan.v2.util.serialization.DefaultPlayFormats
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json._
import play.api.libs.json.ops.JsonImplicits

import scala.util.matching.Regex

/**
 * Convert LocalDateFormat to String
 */
trait StarshipactivateresetLocalDateTimeFormat {
  def localDateTimeAsString(ldt: LocalDateTime): String = {
    val dt = new DateTime(ldt.toDateTime(DateTimeZone.UTC), ldt.getChronology.withZone(DateTimeZone.forOffsetHours(0)))
    ISODateTimeFormat.dateTime.withZoneUTC.print(dt)
  }
}
/**
 * Standard json formats for responses from Starshipactivatereset.
 *
 * The intention here is that all objects which define json formats extend this trait so that we can be sure we're using
 * standardized json formats for things like dates, etc.
 */
trait DefaultStarshipactivateresetFormats extends DefaultPlayFormats with StarshipactivateresetLocalDateTimeFormat with JsonImplicits {

  //Accepts time in any time zone. Writes time in UTC.
  implicit val jodaLocalDateTimeFormat: Format[LocalDateTime] = Format.asString[LocalDateTime](
    { s: String => LocalDateTime.parse(s, ISODateTimeFormat.dateOptionalTimeParser) },
    localDateTimeAsString
  )

  /**
   * provides a default Json [[Format]] for regular expressions. Serializes them as a normal json String, but reads
   * them into a Scala [[Regex]] instance.
   */
  implicit val regExJsonFormat: Format[Regex] = Format.asString(_.r, _.regex)

  /**
   * Create an implicit instance of this case class for types which can be serialized to a string and
   * used as the keys in a Json [[Format]] for a [[Map]].
   * @see [[autoMapFormat]]
   *
   * @param aToStr A function which maps [[A]] into a [[String]]
   * @param strToA A function which reads a [[String]] and returns an [[A]]
   * @tparam A the type you wish to use as a key in a map
   */
  case class MapKeyFormatter[A](aToStr: A => String, strToA: String => A)

  def mapReads[A, B](strToA: String => A)(implicit bReads: Reads[B]): Reads[Map[A, B]] = new Reads[Map[A, B]] {
    def reads(jv: JsValue): JsResult[Map[A, B]] =
      JsSuccess(jv.as[Map[String, B]].map {
        case (k, v) =>
          strToA(k.trim) -> v
      })
  }
  def mapWrites[A, B](aToStr: A => String)(implicit bWrite: Writes[B]): Writes[Map[A, B]] = new Writes[Map[A, B]] {
    def writes(map: Map[A, B]): JsValue =
      Json.obj(map.map {
        case (s, o) =>
          val ret: (String, JsValueWrapper) = aToStr(s) -> bWrite.writes(o)
          ret
      }.toSeq: _*)
  }

  /**
   * This implicit def will allow you to use the [[Json.format]] macro to create a Json [[Format]] for a map
   * of [[A]] -> [[B]] assuming there is an implicit [[MapKeyFormatter]] in scope for [[A]].
   * @see [[MapKeyFormatter]]
   *
   * @param bWrite implicit json writer for [[B]]
   * @param bReads implicit json reader for [[B]]
   * @param keyFormatter implicit [[MapKeyFormatter]] for [[A]]
   * @tparam A the type of the key in the map
   * @tparam B the type of the values in the map
   * @return json [[Format]] for Map[A, B]
   */
  implicit def autoMapFormat[A, B](
    implicit
    bWrite: Writes[B],
    bReads: Reads[B],
    keyFormatter: MapKeyFormatter[A]): Format[Map[A, B]] =
    Format(mapReads[A, B](keyFormatter.strToA), mapWrites[A, B](keyFormatter.aToStr))

}

object DefaultStarshipactivateresetFormats extends DefaultStarshipactivateresetFormats
