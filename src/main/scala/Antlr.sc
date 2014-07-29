package papa_carlos_aunt

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.File
import scala.util.Properties
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher
import io.github.papacarlo._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import io.github.papacarlo.ANTLRv4Parser._
import papa_carlos_aunt.AntlrGrammar._
import io.github.papacarlo.ANTLRv4Parser.LexerRuleContext

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
  val lexer = new ANTLRv4Lexer(new ANTLRInputStream(new BufferedInputStream(input)))
  val parser = new ANTLRv4Parser(new CommonTokenStream(lexer))
  lexer.reset()
  parser.reset()

  parser.reset()

  val r = parser.grammarSpec().rules()
  val lex = r.ruleSpec().map(_.lexerRule()).filterNot(_ == null)
  val allLex = lex.map(_.lexerRuleBlock().lexerAltList())
  val l = allLex(9)
	def lexerRule(r: LexerRuleContext) = {
		val alts = lexerToken(r.lexerRuleBlock().lexerAltList())
		val name = r.TOKEN_REF.getText()
		SLexerRule(name, alts)
	}
  def lexerToken(lexerAltList: LexerAltListContext) = {
    val alternatives = lexerAltList.lexerAlt.asScala
    val res = alternatives.map(_.lexerElements().lexerElement().map(lexerElement2Pc).flatten.toList)
    res.toList
  }
  def lexerElement2Pc(el: LexerElementContext): Option[SLexerElement] = {
    val repetition = Option(el.ebnfSuffix()).map(_.getText())
    Option(el.lexerAtom()).map(a => SLexerElement(a.getText(), repetition))
  }

  lexerToken(l)
  val content = txt.papa_carlo.apply(lex.map(lexerRule))

  allLex.map(lexerToken)
  ANTLRv4Parser.tokenNames.indexWhere(_ == "'*'")
}