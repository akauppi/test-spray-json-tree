//
// build.sbt
//

//name := "test-spray-json-tree"

//scalaVersion  := "2.11.7"

scalacOptions ++= Seq( "-unchecked"
                    , "-deprecation"
                    , "-encoding", "utf8"
                  )

scalacOptions ++= Seq( 
    "-feature",
    "-language", "postfixOps"
    )

libraryDependencies ++= Seq(
    "io.spray" %% "spray-json" % "1.3.1",
    "joda-time" % "joda-time" % "2.7",
    "org.scalatest"       %% "scalatest"     % "3.0.0-SNAP5" % "test"
)
