/*
* JsonTreeTest
*/
// no package

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import spray.json._
import spray.json.DefaultJsonProtocol._

import org.scalatest._

/*
* References:
*   Got good ideas from the *question* of this SO entry:
*   -> http://stackoverflow.com/questions/16512301/convert-polymorphic-case-classes-to-json-and-back
*/
class JsonTreeTest extends FlatSpec with Matchers {

  //---
  // From https://github.com/spray/spray-json
  //
  case class Color(name: String, red: Int, green: Int, blue: Int)

  object ColorJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat4(Color)
  }

  //---
  class EnumLike(val s: String)   // formats as 'JsString' = usable as object key

  object EnumLikeJsonProtocol extends DefaultJsonProtocol {
    implicit object SomeEnumFormat extends JsonFormat[EnumLike] {
      def write(v: EnumLike) = JsString(v.s)

      def read(jsv: JsValue) = jsv match {
        case JsString(s) => new EnumLike(s)
        case _ => deserializationError(s"EnumLike expected - got $jsv")
      }
    }
  }

  //---
  object DateTimeJsonProtocol extends DefaultJsonProtocol {

    // adapted from https://gist.github.com/chronodm/7684755
    //
    implicit object DateTimeFormat extends JsonFormat[DateTime] {

      // The choice of the formatter decides how our dates get shown as 'JsString'.
      //
      // .basicDateTime:  e.g. "20150630T154340.078+0300"
      // .dateTime:       e.g. "2015-06-30T15:46:38.185+03:00"
      //
      // See -> http://joda-time.sourceforge.net/apidocs/org/joda/time/format/ISODateTimeFormat.html
      //
      private
      val formatter = ISODateTimeFormat.dateTime

      def write(o: DateTime): JsValue = {
        JsString(formatter.print(o))
      }

      def read(jsv: JsValue): DateTime = jsv match {
        case JsString(s) =>
          try {
            formatter.parseDateTime(s)
          }
          catch {
            case t: Throwable => err(s)
          }
        case _ =>
          err(jsv.toString)
      }

      private
      def err(v: String): Nothing = {
        val sample = formatter.print(0)
        deserializationError(f"'$v' is not a valid date value. Dates must be in ISO-8601 format, e.g. '$sample'")
      }
    }
  }

  import ColorJsonProtocol._
  import EnumLikeJsonProtocol._
  import DateTimeJsonProtocol._

  // Now our own, using a hierarchy of case classes and options, to model JSON
  //
  // Uncomment the lines, one after the other. We should make them all work.
  //
  case class Top( c: Option[Color]
                  , inner: Option[Top.Inner]
                  , map: Option[Map[EnumLike,Double]]
                  , dt: DateTime
                  //, t: Tuple2[String,String]
                  //, t2: Tuple2[Int,Int]
                  )

  object TopJsonProtocol extends DefaultJsonProtocol {
    implicit val innerFormat = jsonFormat2(Top.Inner)

    // Spray.json note: since we manually defined 'Top' companion object, have to use '.apply'
    //
    implicit val topFormat /*: JsonFormat[Top]*/ = jsonFormat4(Top.apply)
  }

  object Top {
    case class Inner(s: String, c: Color)
  }

  behavior of "Case class <-> JSON conversion"; {
    import TopJsonProtocol._

    val color = Color( "some", 1,2,3 )
    val inner = Top.Inner("xxx", color)
    val top = Top( Some(color), Some(inner), Some(Map(new EnumLike("aaa") -> 900)), DateTime.now )

    val js= top.toJson

    info(js.prettyPrint)

    val top2 = js.convertTo[Top]
  }
}
