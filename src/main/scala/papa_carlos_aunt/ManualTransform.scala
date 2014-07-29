package papa_carlos_aunt

import name.lakhin.eliah.projects.papacarlo.lexis.{ Token, Matcher, Tokenizer, Contextualizer }
import name.lakhin.eliah.projects.papacarlo.{ Syntax, Lexer }
import name.lakhin.eliah.projects.papacarlo.syntax.Rule
import name.lakhin.eliah.projects.papacarlo.syntax.rules.NamedRule

class ManualTransform {
  private def tokenizer = {
    val tokenizer = new Tokenizer()

    import tokenizer._
    import Matcher._

    tokenCategory(
      "WhiteSpace",
      sequence(chunk("\u0020"), chunk("\u0009"), chunk("\u000D"), chunk("\u000A"))).skip

    tokenCategory(
      "whitespace",
      oneOrMore(anyOf(" \t\f\n"))).skip

    tokenCategory(
      "string",
      sequence(
        chunk("\""),
        oneOrMore(choice(
          anyExceptOf("\n\r\\\""),
          sequence(chunk("\\"), anyOf("\"\\/bfnrt")),
          sequence(
            chunk("\\u"),
            repeat(
              choice(rangeOf('a', 'f'), rangeOf('A', 'F'), rangeOf('0', '9')),
              times = 4)))),
        chunk("\"")))

    tokenCategory(
      "number",
      sequence(
        optional(chunk("-")),
        choice(
          chunk("0"),
          sequence(rangeOf('1', '9'), zeroOrMore(rangeOf('0', '9')))),
        optional(sequence(chunk("."), oneOrMore(rangeOf('0', '9')))),
        optional(sequence(
          anyOf("eE"),
          optional(anyOf("+-")),
          oneOrMore(rangeOf('0', '9'))))))

    tokenCategory(
      "alphanum",
      oneOrMore(rangeOf('a', 'z')))

    terminals(",", ":", "{", "}", "[", "]", "//", "/*", "*/")

    keywords("true", "false", "null")

    tokenizer
  }

  private def contextualizer = {
    val contextualizer = new Contextualizer

    import contextualizer._

    trackContext("[", "]").allowCaching
    trackContext("{", "}").allowCaching
    trackContext("//", Token.LineBreakKind).forceSkip.topContext
    trackContext("/*", "*/").forceSkip.topContext

    contextualizer
  }

  def lexer = new Lexer(tokenizer, contextualizer)

  def syntax(lexer: Lexer) = new {
    val syntax = new Syntax(lexer)

    import syntax._
    import Rule._

    val jsonObject = rule("object").cachable.main {
      sequence(
        token("{"),
        zeroOrMore(
          branch("entry", objectEntry),
          separator =
            recover(token(","), "object entries must be separated with , sign")),
        recover(token("}"), "object must end with } sign"))
    }

    val objectEntry = rule("entry") {
      sequence(
        capture("key", token("string")),
        token(":"),
        branch("value", jsonValue))
    }

    val jsonArray = rule("array").cachable {
      sequence(
        token("["),
        zeroOrMore(
          branch("value", jsonValue),
          separator =
            recover(token(","), "array entries must be separated with , sign")),
        recover(token("]"), "array must end with ] sign"))
    }

    val jsonString = rule("string") {
      capture("value", token("string"))
    }

    val jsonNumber = rule("number") {
      capture("value", token("number"))
    }

    val jsonBoolean = rule("boolean") {
      capture("value", choice(token("true"), token("false")))
    }

    val jsonNull = rule("null") {
      token("null")
    }

    val jsonValue: NamedRule = subrule("value") {
      choice(jsonString, jsonNumber, jsonObject, jsonArray, jsonBoolean,
        jsonNull)
    }
  }.syntax
}