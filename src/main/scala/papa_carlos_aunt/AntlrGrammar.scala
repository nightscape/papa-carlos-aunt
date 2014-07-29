package papa_carlos_aunt

object AntlrGrammar {
  case class STerminal(term: String)
  case class SLexerElement(token: String, repetition: Option[String])
  case class SLexerRule(name: String, alternatives: List[List[SLexerElement]])
}