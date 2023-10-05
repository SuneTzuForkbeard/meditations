package sune.tzu.meditations.model

object VoiceDefinitions {
  // Voices at: https://cloud.google.com/text-to-speech
  val allVoices = List(
    VoiceDefinition(name= "narrator", googleId = "en-US-Neural2-C", language = "en-US"),
    VoiceDefinition(name= "junkie", googleId = "en-GB-Neural2-D", language = "en-GB", pitch = Some(-17.6), speed = Some(0.85)),
    VoiceDefinition(name= "mujer", googleId = "es-ES-Neural2-D", language = "es-ES", pitch = Some(0.8), speed = Some(1.05)),
    VoiceDefinition(name= "subject", googleId = "es-ES-Neural2-F", language = "es-ES", pitch = Some(-14.0), speed = Some(1.2)),
    VoiceDefinition(name= "bully1", googleId = "en-GB-Neural2-B", language = "en-GB", pitch = Some(-4.0), speed = Some(1.1)),
    VoiceDefinition(name= "bully2", googleId = "en-GB-Neural2-D", language = "en-GB", pitch = Some(-4.0), speed = Some(1.1)),
    VoiceDefinition(name= "bullygirl", googleId = "en-US-Neural2-E", language = "en-US"),
    VoiceDefinition(name= "boss", googleId = "en-US-Neural2-D", language = "en-US", pitch = Some(-16.5), speed = Some(1.23)),
    VoiceDefinition(name= "ceo", googleId = "en-US-Neural2-J", language = "en-US", pitch = Some(-12.8), speed = Some(0.85))


  )

  val voiceMap = allVoices
    .map(voice => (voice.name, voice))
    .toMap



  case class VoiceDefinition(
                            name : String,
                            googleId : String,
                            language: String,
                            pitch : Option[Double] = None,
                            speed : Option[Double] = None
                            );
}
