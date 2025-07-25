//package org.finawreadmin.project.model
//
//import kotlinx.serialization.*
//import kotlinx.serialization.descriptors.*
//import kotlinx.serialization.encoding.*
//import kotlinx.serialization.json.*
//
//object QuizDeserializer : KSerializer<Quiz?> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Quiz")
//
//    override fun deserialize(decoder: Decoder): Quiz? {
//        val input = decoder as? JsonDecoder ?: throw SerializationException("Expected JsonDecoder")
//        val element = input.decodeJsonElement()
//
//        return when {
//            element is JsonObject -> input.json.decodeFromJsonElement(Quiz.serializer(), element)
//            element is JsonPrimitive && element.isString && element.content.isBlank() -> null
//            else -> null
//        }
//    }
//
//    override fun serialize(encoder: Encoder, value: Quiz?) {
//        if (value != null) {
//            val jsonEncoder = encoder as? JsonEncoder ?: throw SerializationException("Expected JsonEncoder")
//            jsonEncoder.encodeSerializableValue(Quiz.serializer(), value)
//        } else {
//            encoder.encodeNull()
//        }
//    }
//}