package com.example.guia11

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/** Lee la edad aunque el servidor la envíe como texto ("edad 1", "25", etc.). */
class EdadAdapter : TypeAdapter<Int>() {

    override fun write(out: JsonWriter, value: Int?) {
        if (value == null) out.nullValue() else out.value(value.toLong())
    }

    override fun read(reader: JsonReader): Int {
        return when (reader.peek()) {
            JsonToken.NUMBER -> reader.nextDouble().toInt()
            JsonToken.STRING -> reader.nextString().filter { it.isDigit() }.toIntOrNull() ?: 0
            JsonToken.NULL -> { reader.nextNull(); 0 }
            else -> { reader.skipValue(); 0 }
        }
    }
}