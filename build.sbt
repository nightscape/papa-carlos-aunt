organization := "io.github"

name := "papa_carlos_aunt"

version := "0.1.0"

scalaVersion := "2.11.1"

antlr4Settings

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource +  EclipseCreateSrc.Managed

libraryDependencies ++= Seq(
	"name.lakhin.eliah.projects.papacarlo" %% "papa-carlo" % "0.7.0"
)