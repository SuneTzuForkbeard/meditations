package sune.tzu.meditations

import sune.tzu.meditations.model.{PartToSend, SpokenPart}
import sune.tzu.meditations.phonetics.IPAConverter
import sune.tzu.meditations.phonetics.IPAConverter.{IPAError, IPAPhoneme}

import java.io.File
import scala.xml.{Node, NodeSeq, PrettyPrinter, XML}

object SsmlParser {

  private val stringMaxLength = 4500
  private val printer = new PrettyPrinter(2,2)
  private def asString(node : Node) : String = printer.format(node)
  private def asString(nodes : NodeSeq) : String = asString(nodes.asInstanceOf[Node])

  def parse(file : File) = {
    val elem = XML.loadFile(file)
    val spokenParts = (elem \ "spokenPart").map {
      case partElem => {
        val translate = (partElem \@ "translate").trim.toLowerCase == "true"
        SpokenPart(
          (partElem \@ "spokenBy"),
          (partElem \@ "name"),
          parsePElems(partElem \ "p", translate),
          translate
        )
      }
    }
    spokenParts
  }

  def parsePElems(elem :  Seq[Node], convertToPhonemes : Boolean) : Seq[PartToSend] = {
    val withStrings = elem
      .map(el => (el, parseOrConvert(el, convertToPhonemes)))
      .map(p => (p._1, p._2, p._2.length))
    var takenLength = 0
    val inThisRound = withStrings.takeWhile{
      case (node, ssml, ssmlLength) => {
        if(takenLength + ssmlLength < stringMaxLength){
          takenLength += ssmlLength
          true
        }
        else false
    }}
    val fromThis = PartToSend(inThisRound.map(_._2).mkString("\r\n"))
    val inNextRound = withStrings.drop(inThisRound.size)
    if(!inNextRound.isEmpty) {
      val fromNext = parsePElems(inNextRound.map(_._1), convertToPhonemes)
      fromThis +: fromNext
    }
    else List(fromThis)
  }

  private val splitValues = List(
    "," -> ",",
    "[.]" -> ".",
    "\\s" -> "\\r\\n"
  )
  private val assemblers = splitValues
    .map(_._2)
    .toSet

  private def parseOrConvert(node : Node, convertToIpa : Boolean) : String = {
    var stringVal = asString(node)
    if(convertToIpa){
      val childString = node.child.text
      val tokens = splitValues
        .foldLeft(List(childString)) {
          case (curLis, (splitter, assembler)) => curLis.flatMap {
            case str if str.contains(splitter) => (str.split(splitter).flatMap(part => List(part, assembler)))
              .toList
              .reverse
              .drop(1)
              .reverse
            case str => List(str)
          }
        }
      val replaced = tokens.flatMap {
        case str if assemblers(str) => List(str)
        case str => {
          val converted = IPAConverter.translate(str)
          converted.collect {
            case IPAError(errorWord) => errorWord
          }.foreach(word => println(s"  failed to IPA-translate: $word"))
          converted.collect {
            case IPAPhoneme(inputWord, phStr) => s"""<phoneme alphabet="ipa" ph="$phStr">$inputWord</phoneme>"""
          }
        }
      }

      s"""<p>
         |  ${replaced.mkString("\r\n  ")}
         |</p>""".stripMargin
    }
    else stringVal
  }

  //def splitWords(inp)



}
