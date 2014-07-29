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
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.RuleNode
import AntlrGrammar._

object AntlrVisitor {

  class GrammarVisitor extends ANTLRv4ParserBaseVisitor[Any] {
    //override def visitLexerAtom(atom: LexerAtomContext): Any = {
    //  SLexerRule(atom.getText())
    //}
    //override def shouldVisitNextChild(node: RuleNode, currentResult: Any) = currentResult match {
		//	case null => true
		//	case t: STerminal => true
		//	case l: List[_] => true
    //}
    //override def visitTerminal(term: TerminalNode): Any = {
    //  STerminal(term.getText)
    //}
    override def visitLexerAltList(lexerAltList: LexerAltListContext): String = {
    val children = super.visitLexerAltList(lexerAltList)
    println("Childrnen", children)
    val alternatives = lexerAltList.lexerAlt.asScala
    println("Alternati", alternatives)
    val res = alternatives.map { a =>
      println("Here", a.lexerElements().lexerElement().map(_.getText()))
      a.lexerElements().lexerElement().map(visitLexerElement).mkString(",")
    }.mkString("|")
    res
  }
    override def aggregateResult(agg: Any, nextResult: Any) = (agg, nextResult) match {
      case (null, None) => List()
      case (null, Some(x)) => List(x)
      case (null, n) => n
      case (l: List[_], null) => l
      case (l: List[_], None) => l
      case (l: List[_], Some(x)) => x :: l
      case (l: List[_], a: Any) => a :: l
      case (l: List[_], a: List[_]) => a ++ l
      case (a, n) => List(a,n)
    }
    override def visitLexerElement(el: LexerElementContext) = {
      val repetition = Option(el.ebnfSuffix()).map(_.getText())
    	val children = super.visitLexerElement(el)
    	children match {
    		case t: STerminal => SLexerElement(t.term, repetition)
    		case l: List[_] if l.forall(_.isInstanceOf[STerminal]) => l
    		case l: List[_] if l.forall(_.isInstanceOf[Option[_]]) => l.asInstanceOf[List[Option[Any]]].flatten
    		case null => STerminal("empty")
    	}
    	Option(el.lexerAtom()).map(a => SLexerElement(a.getText(), repetition))
    }
  }
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  // Yuck.... See: https://github.com/scala-ide/scala-worksheet/issues/102
  import Properties._
  val pathSep = propOrElse("path.separator", ":") //> pathSep  : String = :
  val fileSep = propOrElse("file.separator", "/") //> fileSep  : String = /
  val projectDir = javaClassPath.split(pathSep).
    filter(_.matches(".*worksheet.bin$")).head.
    split(fileSep).dropRight(2).mkString(fileSep) //> projectDir  : String = /Users/moe/Workspaces/scala/papa_carlos_aunt

  val scalaGrammarFile = new File(projectDir, "src/main/antlr4/scala.g4")
                                                  //> scalaGrammarFile  : java.io.File = /Users/moe/Workspaces/scala/papa_carlos_
                                                  //| aunt/src/main/antlr4/scala.g4
  val input = new FileInputStream(scalaGrammarFile)
                                                  //> input  : java.io.FileInputStream = java.io.FileInputStream@655b4432
  val lexer = new ANTLRv4Lexer(new ANTLRInputStream(new BufferedInputStream(input)))
                                                  //> lexer  : io.github.papacarlo.ANTLRv4Lexer = io.github.papacarlo.ANTLRv4Lexe
                                                  //| r@20f92649
  val parser = new ANTLRv4Parser(new CommonTokenStream(lexer))
                                                  //> parser  : io.github.papacarlo.ANTLRv4Parser = io.github.papacarlo.ANTLRv4Pa
                                                  //| rser@38d24866
  lexer.reset()
  parser.reset()
  val visitor = new GrammarVisitor()              //> visitor  : papa_carlos_aunt.AntlrVisitor.GrammarVisitor = papa_carlos_aunt.
                                                  //| AntlrVisitor$GrammarVisitor@53c7a917
  visitor.visit(parser.grammarSpec())             //> (Childrnen,List(SLexerElement(HexDigit,None), SLexerElement(HexDigit,None),
                                                  //|  SLexerElement(HexDigit,None), SLexerElement(HexDigit,None), SLexerElement(
                                                  //| 'u',Some(?)), SLexerElement('u',None), SLexerElement('\\',None)))
                                                  //| (Alternati,Buffer([350 348 345 243 236 132]))
                                                  //| (Here,ArrayBuffer('\\', 'u', 'u'?, HexDigit, HexDigit, HexDigit, HexDigit))
                                                  //| 
                                                  //| (Childrnen,List(List(SLexerElement('\u000A',None)), List(SLexerElement('\u0
                                                  //| 00D',None)), List(SLexerElement('\u0009',None)), SLexerElement('\u0020',Non
                                                  //| e)))
                                                  //| (Alternati,Buffer([350 348 345 243 236 132], [352 348 345 243 236 132], [35
                                                  //| 2 348 345 243 236 132], [352 348 345 243 236 132]))
                                                  //| (Here,ArrayBuffer('\u0020'))
                                                  //| (Here,ArrayBuffer('\u0009'))
                                                  //| (Here,ArrayBuffer('\u000D'))
                                                  //| (Here,ArrayBuffer('\u000A'))
                                                  //| (Childrnen,List(List(SLexerElement('}',None)), List(SLexerElement('{',None)
                                                  //| ), List(SLexerElement(']',None)), List(SLexerElement('[',None)), List(SLexe
                                                  //| rElement(')',Non
                                                  //| Output exceeds cutoff limit.

}