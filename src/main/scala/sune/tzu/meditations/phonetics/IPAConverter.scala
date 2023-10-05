package sune.tzu.meditations.phonetics

import org.apache.commons.io.FileUtils

import java.io.File
import java.nio.charset.StandardCharsets
import scala.io.{Codec, Source}

object IPAConverter {

  lazy val arpaDictionary = readCmuDictionary
  private def readCmuDictionary = {
    // See:  http://www.speech.cs.cmu.edu/cgi-bin/cmudict?in=C+M+U+Dictionary

    val inputFileName = this.getClass.getClassLoader.getResource("cmudict-07b.txt").getFile
    val lines = FileUtils.readFileToString(new File(inputFileName), "utf-8")
      .split("\n")
      .filter(!_.startsWith(";;;"))

    var returnee = lines
      .map(line => line.split(" ").filter(_.length > 0).toList match {
        case Nil => None
        case car::Nil => None
        case car::cldr => Some(car -> cldr.map(Phoneme))
      }).collect {
        case Some(mapping) => mapping
      }.toMap

    returnee
  }

  def testCompleteness(): Unit = {
    arpaDictionary.foreach {
      case (key, phonemes) => {
        val notMapped = phonemes
          .map(_.str)
          .filter(ph => !phonemeMap.contains(ph))
        if(notMapped.nonEmpty){
          println(s"Phonemes: ${notMapped.mkString(", ")} not mapped... Used in: ${key}")
        }

      }
    }
  }

  def translate(inputText : String) : List[IPAResult] = inputText.split("(\r)|(\n)")
    .filter(_.trim.nonEmpty)
    .map(line => line.split("\\.").map(
      sentence => sentence.split(",").map(
        withinComma => withinComma.split("\\s")
          .map(_.trim)
          .filter(_.nonEmpty)
          .map(word => translateWord(word) match {
            case Some(res) => IPAPhoneme(word, res)
            case None => IPAError(word)
          })
      ).flatten
    ).flatten
    ).flatten.toList

  private def translateWord(inputWord : String) : Option[String] = {
    val word = inputWord.toUpperCase
    val asArpa = translateToArpa(word)
    asArpa.map {
      case lis => translateFromArpa(lis)
    }
  }

  private def translateToArpa(word : String) = {
    arpaDictionary.get(word) match {
      case Some(res) => Some(res)
      case None => {
        val bySplitAndConvert = splitAndConvertToArpaLazySolution(word)
        bySplitAndConvert match {
          case Some(res) => Some(res)
          case _ => None
        }
      }
    }
  }

  private def splitAndConvertToArpa(str : String) : Option[List[Phoneme]] = {
    if(arpaDictionary.contains(str))
      Some(arpaDictionary(str))
    else if(str.isEmpty)
       None
    else {
      var sizeOfLatter = 1
      var returnee : Option[List[Phoneme]] = None
      while(sizeOfLatter < str.length && returnee.isEmpty) {
        val (first, latter) = str.splitAt(str.length - 1 - sizeOfLatter)
        val (firstRes, latterRes) = (splitAndConvertToArpaLazySolution(first), splitAndConvertToArpa(latter))
        (firstRes, latterRes) match {
          case (Some(fRes), Some(lRes)) => returnee = Some(fRes ++ lRes)
          case _ =>
        }
        sizeOfLatter = sizeOfLatter +1
        println(s"$first, $latter")
      }
      returnee
     }
  }

  private def splitAndConvertToArpaLazySolution(str: String): Option[List[Phoneme]] = {
    if (arpaDictionary.contains(str))
      Some(arpaDictionary(str))
    else {
      println("ARPA dictionary did not contain: " + str)
      None
    }

  }

  private def translateFromArpa(phonemes : Seq[Phoneme]) = phonemes
    .map(ph => phonemeMap(ph.str))
    .mkString("")

/*  private val phonemeMap = Map(
    ("AA","ɑ~ɒ"),
    ("AE","æ"),
    ("AH","ʌ"),
    ("AO","ɔ"),
    ("AW","aʊ"),
    ("AX","ə"),
    ("AXR[3]","ɚ"),
    ("AY","aɪ"),
    ("EH","ɛ"),
    ("ER","ɝ"),
    ("EY","eɪ"),
    ("IH","ɪ"),
    ("IX","ɨ"),
    ("IY","i"),
    ("OW","oʊ"),
    ("OY","ɔɪ"),
    ("UH","ʊ"),
    ("UW","u"),
    ("UX[3]","ʉ"),
    ("B","b"),
    ("CH","tʃ"),
    ("D","d"),
    ("DH","ð"),
    ("DX","ɾ"),
    ("EL","l̩"),
    ("EM","m̩"),
    ("EN","n̩"),
    ("F","f"),
    ("G","ɡ"),
    ("HH","h"),
    ("H[3]","h"),
    ("JH","dʒ"),
    ("K","k"),
    ("L","l"),
    ("M","m"),
    ("N","n"),
    ("NX","ŋ"),
    ("NG[3]","ŋ"),
    ("NX[3]","ɾ̃"),
    ("P","p"),
    ("Q","ʔ"),
    ("R","ɹ"),
    ("S","s"),
    ("SH","ʃ"),
    ("T","t"),
    ("TH","θ"),
    ("V","v"),
    ("W","w"),
    ("WH","ʍ"),
    ("Y","j"),
    ("Z","z"),
    ("ZH","ʒ")
  )*/

  private val phonemeMap = Map(
    ("AO","ɔ"),
    ("AO0","ɔ"),
    ("AO1","ɔ"),
    ("AO2","ɔ"),
    ("AA","ɑ"),
    ("AA0","ɑ"),
    ("AA1","ɑ"),
    ("AA2","ɑ"),
    ("IY","i"),
    ("IY0","i"),
    ("IY1","i"),
    ("IY2","i"),
    ("UW","u"),
    ("UW0","u"),
    ("UW1","u"),
    ("UW2","u"),
    ("EH","e"), // modern versions use "e" instead of "ɛ"
    ("EH0","e"), // ɛ
    ("EH1","e"), // ɛ
    ("EH2","e"), // ɛ
    ("IH","ɪ"),
    ("IH0","ɪ"),
    ("IH1","ɪ"),
    ("IH2","ɪ"),
    ("UH","ʊ"),
    ("UH0","ʊ"),
    ("UH1","ʊ"),
    ("UH2","ʊ"),
    ("AH","ʌ"),
    ("AH0","ə"),
    ("AH1","ʌ"),
    ("AH2","ʌ"),
    ("AE","æ"),
    ("AE0","æ"),
    ("AE1","æ"),
    ("AE2","æ"),
    ("AX","ə"),
    ("AX0","ə"),
    ("AX1","ə"),
    ("AX2","ə"),
    ("EY","eɪ"),
    ("EY0","eɪ"),
    ("EY1","eɪ"),
    ("EY2","eɪ"),
    ("AY","aɪ"),
    ("AY0","aɪ"),
    ("AY1","aɪ"),
    ("AY2","aɪ"),
    ("OW","oʊ"),
    ("OW0","oʊ"),
    ("OW1","oʊ"),
    ("OW2","oʊ"),
    ("AW","aʊ"),
    ("AW0","aʊ"),
    ("AW1","aʊ"),
    ("AW2","aʊ"),
    ("OY","ɔɪ"),
    ("OY0","ɔɪ"),
    ("OY1","ɔɪ"),
    ("OY2","ɔɪ"),
    ("P","p"),
    ("B","b"),
    ("T","t"),
    ("D","d"),
    ("K","k"),
    ("G","g"),
    ("CH","tʃ"),
    ("JH","dʒ"),
    ("F","f"),
    ("V","v"),
    ("TH","θ"),
    ("DH","ð"),
    ("S","s"),
    ("Z","z"),
    ("SH","ʃ"),
    ("ZH","ʒ"),
    ("HH","h"),
    ("M","m"),
    ("N","n"),
    ("NG","ŋ"),
    ("L","l"),
    ("R","r"),
    ("ER","ɜr"),
    ("ER0","ɜr"),
    ("ER1","ɜr"),
    ("ER2","ɜr"),
    ("AXR","ər"),
    ("AXR0","ər"),
    ("AXR1","ər"),
    ("AXR2","ər"),
    ("W", "w"),
    ("Y", "j")
  )

  case class Phoneme(str : String)

  sealed trait IPAResult
  case class IPAPhoneme(inputWord : String, phStr : String) extends IPAResult
  case class IPAError(errorWord : String) extends IPAResult



}
