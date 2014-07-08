import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.File
import scala.util.Properties
import org.antlr.v4.runtime.tree.ParseTreeWalker

object Antlr {
  println("Welcome to the Scala worksheet")
  // Yuck.... See: https://github.com/scala-ide/scala-worksheet/issues/102
  import Properties._
  val pathSep = propOrElse("path.separator", ":")
  val fileSep = propOrElse("file.separator", "/")
  val projectDir = javaClassPath.split(pathSep).
    filter(_.matches(".*worksheet.bin$")).head.
    split(fileSep).dropRight(2).mkString(fileSep)

  val scalaGrammarFile = new File(projectDir, "src/main/antlr4/scala.g4")
  val input = new FileInputStream(scalaGrammarFile)
  val lexer = new ANTLRv4Lexer(new ANTLRInputStream(new BufferedInputStream(input)));
  val parser = new ANTLRv4Parser(new CommonTokenStream(lexer));

  val tree = parser.grammarSpec();
  class ScalaListener extends ANTLRv4ParserBaseListener {
    override def enterParserRuleSpec(ctx: ANTLRv4Parser.ParserRuleSpecContext) {
      println(s"Entering parser rule $ctx: ${ctx.getText()}")
      
    }
  }
  val listener = new ScalaListener
  val walker = new ParseTreeWalker()
  lexer.reset()
  parser.reset()
  walker.walk(listener, parser.grammarSpec())
  //val visitor = new RuleVisitor();
  //visitor.visit(tree);

  //println(visitor.getRules())
}