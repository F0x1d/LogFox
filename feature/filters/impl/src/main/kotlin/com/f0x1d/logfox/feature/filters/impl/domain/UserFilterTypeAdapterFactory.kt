package com.f0x1d.logfox.feature.filters.impl.domain

import com.f0x1d.logfox.feature.filters.api.model.MatchData
import com.f0x1d.logfox.feature.filters.api.model.MatchMode
import com.f0x1d.logfox.feature.filters.api.model.UserFilter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * Reads [UserFilter] from JSON, upgrading filter files exported by older releases.
 *
 * Before match modes existed, `tag` and `content` were plain strings. The matching they implied
 * differed by field — tag was exact equality, content was a substring match — so legacy strings are
 * upgraded to the [MatchMode] that preserves each field's old behavior. This mirrors the database
 * migrations added alongside [MatchData] (tag -> EXACT, content -> CONTAINS), keeping file import and
 * stored filters consistent.
 *
 * Current exports already write `tag`/`content` as [MatchData] objects; those pass through to the
 * delegate adapter unchanged, as does the rest of the object. Implemented as a [TypeAdapterFactory]
 * so it can delegate to Gson's reflective adapter without recursing into itself; register it on an
 * import-scoped Gson only, so the shared app Gson stays generic.
 */
class UserFilterTypeAdapterFactory : TypeAdapterFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType != UserFilter::class.java) return null

        val delegate = gson.getDelegateAdapter(this, type) as TypeAdapter<UserFilter>
        val elementAdapter = gson.getAdapter(JsonElement::class.java)
        val matchDataAdapter = gson.getAdapter(MatchData::class.java)

        return object : TypeAdapter<UserFilter>() {
            override fun write(out: JsonWriter, value: UserFilter?) = delegate.write(out, value)

            override fun read(reader: JsonReader): UserFilter? {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull()
                    return null
                }

                val obj = elementAdapter.read(reader).asJsonObject
                upgradeLegacyString(obj, "tag", MatchMode.EXACT, matchDataAdapter)
                upgradeLegacyString(obj, "content", MatchMode.CONTAINS, matchDataAdapter)
                return delegate.fromJsonTree(obj)
            }
        } as TypeAdapter<T>
    }

    // Rewrites a legacy string field into a MatchData object so the delegate adapter sees the current
    // shape. Object values are already MatchData and are left untouched. The MatchData is serialized
    // through its own adapter rather than hand-built, so the JSON shape can't drift from the class.
    private fun upgradeLegacyString(
        obj: JsonObject,
        field: String,
        legacyMode: MatchMode,
        matchDataAdapter: TypeAdapter<MatchData>,
    ) {
        val element = obj.get(field) ?: return
        if (!element.isJsonPrimitive || !element.asJsonPrimitive.isString) return

        obj.add(field, matchDataAdapter.toJsonTree(MatchData(value = element.asString, matchMode = legacyMode)))
    }
}
