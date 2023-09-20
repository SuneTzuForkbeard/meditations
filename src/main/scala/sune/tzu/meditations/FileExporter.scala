package sune.tzu.meditations

import org.apache.commons.io.FileUtils
import sune.tzu.meditations.model.GeneratedAudio

import java.io.File
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileExporter {

  private val outFolderTimestampFormat =  DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss")

  def exportData(inputFileName : String, inputFileContent : String, generationResults : Seq[GeneratedAudio], outputFolder : File): Unit = {
    val localTime = LocalDateTime.now()
    val outFolder = new File(outputFolder, outFolderTimestampFormat.format(localTime))
    outFolder.mkdirs()
    FileUtils.write(new File(outFolder, inputFileName), inputFileContent, "UTF-8")
    generationResults.zipWithIndex.foreach {
      case (GeneratedAudio(part, ssml, bytes), index) => {
        val prefix = if(index < 10) "0" + index else index.toString
        val partNamePrefix = prefix + "_" + part.partName
        val ssmlFile = new File(outFolder, partNamePrefix + ".ssml")
        val audioFile = new File(outFolder, partNamePrefix + ".mp3")
        FileUtils.write(ssmlFile, ssml, "UTF-8")
        FileUtils.writeByteArrayToFile(audioFile, bytes)
      }
    }


  }


}
