// no package

import spray.json._
import spray.json.DefaultJsonProtocol._

import org.scalatest._

//import org.joda.time.DateTime

/*
*/
class JsonTreeTest extends FlatSpec with Matchers {

  // From https://github.com/spray/spray-json
  //
  case class Color(name: String, red: Int, green: Int, blue: Int)

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat4(Color)
  }

  import MyJsonProtocol._

  val json = Color("CadetBlue", 95, 158, 160).toJson
  val color = json.convertTo[Color]

  // Now our own, using a hierarchy of case classes and options, to model JSON
  //
  case class Top(
                  c: Option[Color]
                  //, inner: Top.Inner
                  //, inner2: Option[Top.Inner]
                  //, map: Option[Map[CustomThatFormatsAsJsString,Double]]
                  //, dt: DateTime
                  )

  object TopJsonProtocol extends DefaultJsonProtocol {
    import Top.InnerJsonProtocol._

    implicit val topFormat = jsonFormat1(Top.apply)   // Spray.json note: since we manually defined 'Top' companion object, have to use '.apply'
  }

  object Top {
    case class Inner(s: String, c: Color)

    object InnerJsonProtocol extends DefaultJsonProtocol {
      implicit val innerFormat = jsonFormat2(Inner)
    }
  }

  behavior of "Case class <-> JSON conversion"; {
    import TopJsonProtocol._

    val color = Color( "some", 1,2,3 )
    val inner = Top.Inner("xxx", color)
    val top = Top( Some(color) /*, inner*/ )

    val js= top.toJson

    info(js.prettyPrint)

    val top2 = js.convertTo[Top]
  }
}
