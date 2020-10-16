// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.fragment_used_twice

import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.apollographql.apollo.api.internal.ResponseReader
import com.example.fragment_used_twice.type.CustomType
import kotlin.Any
import kotlin.Array
import kotlin.String
import kotlin.Suppress

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
internal object TestQuery_ResponseAdapter : ResponseAdapter<TestQuery.Data> {
  private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
    ResponseField.forObject("hero", "hero", null, true, null)
  )

  override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Data {
    return reader.run {
      var hero: TestQuery.Hero? = null
      while(true) {
        when (selectField(RESPONSE_FIELDS)) {
          0 -> hero = readObject<TestQuery.Hero>(RESPONSE_FIELDS[0]) { reader ->
            TestQuery_ResponseAdapter.Hero_ResponseAdapter.fromResponse(reader)
          }
          else -> break
        }
      }
      TestQuery.Data(
        hero = hero
      )
    }
  }

  object HeroDetailsCharacterDetailsImpl_ResponseAdapter :
      ResponseAdapter<TestQuery.HeroDetailsCharacterDetailsImpl> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null),
      ResponseField.forCustomType("birthDate", "birthDate", null, false, CustomType.DATE, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?):
        TestQuery.HeroDetailsCharacterDetailsImpl {
      return reader.run {
        var __typename: String? = __typename
        var name: String? = null
        var birthDate: Any? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> name = readString(RESPONSE_FIELDS[1])
            2 -> birthDate = readCustomType<Any>(RESPONSE_FIELDS[2] as ResponseField.CustomTypeField)
            else -> break
          }
        }
        TestQuery.HeroDetailsCharacterDetailsImpl(
          __typename = __typename!!,
          name = name!!,
          birthDate = birthDate!!
        )
      }
    }
  }

  object HeroDetailsHumanDetailsCharacterDetailsImpl_ResponseAdapter :
      ResponseAdapter<TestQuery.HeroDetailsHumanDetailsCharacterDetailsImpl> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null),
      ResponseField.forCustomType("birthDate", "birthDate", null, false, CustomType.DATE, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?):
        TestQuery.HeroDetailsHumanDetailsCharacterDetailsImpl {
      return reader.run {
        var __typename: String? = __typename
        var name: String? = null
        var birthDate: Any? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> name = readString(RESPONSE_FIELDS[1])
            2 -> birthDate = readCustomType<Any>(RESPONSE_FIELDS[2] as ResponseField.CustomTypeField)
            else -> break
          }
        }
        TestQuery.HeroDetailsHumanDetailsCharacterDetailsImpl(
          __typename = __typename!!,
          name = name!!,
          birthDate = birthDate!!
        )
      }
    }
  }

  object OtherHero_ResponseAdapter : ResponseAdapter<TestQuery.OtherHero> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.OtherHero {
      return reader.run {
        var __typename: String? = __typename
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            else -> break
          }
        }
        TestQuery.OtherHero(
          __typename = __typename!!
        )
      }
    }
  }

  object Hero_ResponseAdapter : ResponseAdapter<TestQuery.Hero> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Hero {
      val typename = __typename ?: reader.readString(RESPONSE_FIELDS[0])
      return when(typename) {
        "Droid" -> TestQuery_ResponseAdapter.HeroDetailsCharacterDetailsImpl_ResponseAdapter.fromResponse(reader, typename)
        "Human" -> TestQuery_ResponseAdapter.HeroDetailsHumanDetailsCharacterDetailsImpl_ResponseAdapter.fromResponse(reader, typename)
        else -> TestQuery_ResponseAdapter.OtherHero_ResponseAdapter.fromResponse(reader, typename)
      }
    }
  }
}