// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.recursive_selection

import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.apollographql.apollo.api.internal.ResponseReader
import kotlin.Array
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
internal object TestQuery_ResponseAdapter : ResponseAdapter<TestQuery.Data> {
  private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
    ResponseField.forObject("tree", "tree", null, true, null)
  )

  override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Data {
    return reader.run {
      var tree: TestQuery.Tree? = null
      while(true) {
        when (selectField(RESPONSE_FIELDS)) {
          0 -> tree = readObject<TestQuery.Tree>(RESPONSE_FIELDS[0]) { reader ->
            TestQuery_ResponseAdapter.Tree_ResponseAdapter.fromResponse(reader)
          }
          else -> break
        }
      }
      TestQuery.Data(
        tree = tree
      )
    }
  }

  object Child_ResponseAdapter : ResponseAdapter<TestQuery.Child> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Child {
      return reader.run {
        var __typename: String? = __typename
        var name: String? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> name = readString(RESPONSE_FIELDS[1])
            else -> break
          }
        }
        TestQuery.Child(
          __typename = __typename!!,
          name = name!!
        )
      }
    }
  }

  object Parent_ResponseAdapter : ResponseAdapter<TestQuery.Parent> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Parent {
      return reader.run {
        var __typename: String? = __typename
        var name: String? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> name = readString(RESPONSE_FIELDS[1])
            else -> break
          }
        }
        TestQuery.Parent(
          __typename = __typename!!,
          name = name!!
        )
      }
    }
  }

  object Tree_ResponseAdapter : ResponseAdapter<TestQuery.Tree> {
    private val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField.forString("__typename", "__typename", null, false, null),
      ResponseField.forString("name", "name", null, false, null),
      ResponseField.forList("children", "children", null, false, null),
      ResponseField.forObject("parent", "parent", null, true, null)
    )

    override fun fromResponse(reader: ResponseReader, __typename: String?): TestQuery.Tree {
      return reader.run {
        var __typename: String? = __typename
        var name: String? = null
        var children: List<TestQuery.Child>? = null
        var parent: TestQuery.Parent? = null
        while(true) {
          when (selectField(RESPONSE_FIELDS)) {
            0 -> __typename = readString(RESPONSE_FIELDS[0])
            1 -> name = readString(RESPONSE_FIELDS[1])
            2 -> children = readList<TestQuery.Child>(RESPONSE_FIELDS[2]) { reader ->
              reader.readObject<TestQuery.Child> { reader ->
                TestQuery_ResponseAdapter.Child_ResponseAdapter.fromResponse(reader)
              }
            }?.map { it!! }
            3 -> parent = readObject<TestQuery.Parent>(RESPONSE_FIELDS[3]) { reader ->
              TestQuery_ResponseAdapter.Parent_ResponseAdapter.fromResponse(reader)
            }
            else -> break
          }
        }
        TestQuery.Tree(
          __typename = __typename!!,
          name = name!!,
          children = children!!,
          parent = parent
        )
      }
    }
  }
}