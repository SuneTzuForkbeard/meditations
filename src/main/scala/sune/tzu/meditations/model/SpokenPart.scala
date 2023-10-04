package sune.tzu.meditations.model

case class SpokenPart(
                     spokenBy : String,
                     partName : String,
                     partsToSend : Seq[PartToSend],
                     translateToEnglish : Boolean = false
                     )
