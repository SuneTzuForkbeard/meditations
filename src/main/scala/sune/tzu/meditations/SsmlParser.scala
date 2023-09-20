package sune.tzu.meditations

import sune.tzu.meditations.model.{PartToSend, SpokenPart}

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
      case partElem => SpokenPart(
        (partElem \@ "spokenBy"),
        (partElem \@ "name"),
        parsePElems(partElem \ "p")
      )
    }
    spokenParts
  }

  def parsePElems(elem :  Seq[Node]) : Seq[PartToSend] = {
    val withStrings = elem
      .map(el => (el, asString(el)))
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
      val fromNext = parsePElems(inNextRound.map(_._1))
      fromThis +: fromNext
    }
    else List(fromThis)


  }



}
