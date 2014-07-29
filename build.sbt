organization := "io.github"

name := "papa_carlos_aunt"

version := "0.1.0"

scalaVersion := "2.11.2"

antlr4Settings

antlr4GenVisitor in Antlr4 := true

antlr4PackageName in Antlr4 := Some("io.github.papacarlo")

lazy val root = (project in file(".")).enablePlugins(SbtTwirl)

TwirlKeys.templateImports ++= Seq(
	"scala.collection.JavaConversions._",
	"io.github.papacarlo._",
	"papa_carlos_aunt.AntlrGrammar._",
	"io.github.papacarlo.ANTLRv4Parser._"
)

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource +  EclipseCreateSrc.Managed

libraryDependencies ++= Seq(
	"name.lakhin.eliah.projects.papacarlo" %% "papa-carlo" % "0.7.0"
)