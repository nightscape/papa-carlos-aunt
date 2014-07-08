papa-carlos-aunt
================

Use ANTLR4 grammars for creating incremental Papa Carlo parsers

Getting started
---------------
```
sbt
# Then, in the SBT shell
antlr4:antlr4Generate
eclipse with-source=true
```
After that you need to fix an error in the generated ```scalaLexer.java``` by removing the content of the ```MultiLineChars_action``` method, then you're good to go!
