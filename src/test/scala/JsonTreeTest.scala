/*
* JsonTreeTest
*/
// no package

import spray.json._
import spray.json.DefaultJsonProtocol._

import org.scalatest._

//import org.joda.time.DateTime

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

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat4(Color)
  }

  import MyJsonProtocol._

  //val json = Color("CadetBlue", 95, 158, 160).toJson
  //val color = json.convertTo[Color]

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

  import EnumLikeJsonProtocol._
  
  // Now our own, using a hierarchy of case classes and options, to model JSON
  //
  // Uncomment the lines, one after the other. We should make them all work.
  //
  case class Top( c: Option[Color]
                  , inner: Option[Top.Inner]
                  , map: Option[Map[EnumLike,Double]]
                  //, dt: DateTime
                  )

  object TopJsonProtocol extends DefaultJsonProtocol {
    //import Top.InnerJsonProtocol._    // (does not matter for the error below)

    implicit val innerFormat = jsonFormat2(Top.Inner)

    // Spray.json note: since we manually defined 'Top' companion object, have to use '.apply'
    //
    implicit val topFormat /*: JsonFormat[Top]*/ = jsonFormat3(Top.apply)
  }

  object Top {
    case class Inner(s: String, c: Color)
  }

  behavior of "Case class <-> JSON conversion"; {
    import TopJsonProtocol._

    val color = Color( "some", 1,2,3 )
    val inner = Top.Inner("xxx", color)
    val top = Top( Some(color), Some(inner), Some(Map(new EnumLike("aaa") -> 900)) )

    val js= top.toJson

    info(js.prettyPrint)

    val top2 = js.convertTo[Top]
  }
}
