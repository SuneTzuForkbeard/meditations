package sune.tzu.meditations

import org.apache.commons.io.FileUtils

import java.io.{File}

object GoogleTextToSpeechConverter {

  private val dataFolder = new File("""C:\Users\sune_\OneDrive\Dokumenter\dharma\meditations\Chapter05-TheStorm""")
  private val fileNamePrefix = "Chapter 05-TheStorm"
  private val inputFile = new File(dataFolder, s"$fileNamePrefix.txt")
  private val outputFolder = new File(s"c:\\temp\\meditation-gen\\$fileNamePrefix")
  if(!outputFolder.exists)
    outputFolder.mkdirs()

  def main(args: Array[String]): Unit = {
    val inputFileText = FileUtils.readFileToString(inputFile, "UTF-8")
    val spokenParts = SsmlParser.parse(inputFile)
    GoogleAPI.generateAudio(spokenParts) match {
      case Left(errs) => {
        println("Following errors occurred:")
        errs.foreach(err => println(s" $err"))
      }
      case Right(res) => {
        FileExporter.exportData(inputFile.getName, inputFileText, res, outputFolder)
      }
    }



  }

}
