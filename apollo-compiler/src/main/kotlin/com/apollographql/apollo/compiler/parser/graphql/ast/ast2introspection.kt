import com.apollographql.apollo.compiler.parser.error.ParseException
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLBooleanValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLDirective
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLDocument
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLEnumTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLEnumValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLEnumValueDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLFieldDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLFloatValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLInputObjectTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLInputValueDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLIntValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLInterfaceTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLListType
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLListValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLNamedType
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLNonNullType
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLNullValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLObjectTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLObjectValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLScalarTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLSchemaDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLStringValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLType
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLUnionTypeDefinition
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLValue
import com.apollographql.apollo.compiler.parser.graphql.ast.GQLVariableValue
import com.apollographql.apollo.compiler.parser.introspection.IntrospectionSchema

private class IntrospectionSchemaBuilder(private val document: GQLDocument) {
  private val typeDefinitions = document.definitions.filterIsInstance<GQLTypeDefinition>().map { it.name to it }.toMap()

  fun toIntrospectionSchema() = document.toIntrospectionSchema()

  private fun GQLDocument.toIntrospectionSchema(): IntrospectionSchema {
    return IntrospectionSchema(
        queryType = rootOperationTypeName("query") ?: throw IllegalStateException("No query root operation type found"),
        mutationType = rootOperationTypeName("mutation"),
        subscriptionType = rootOperationTypeName("subscription"),
        types = definitions.filterIsInstance<GQLTypeDefinition>().map {
          it.name to when (it) {
            is GQLObjectTypeDefinition -> it.toSchemaType()
            is GQLInputObjectTypeDefinition -> it.toSchemaType()
            is GQLInterfaceTypeDefinition -> it.toSchemaType()
            is GQLScalarTypeDefinition -> it.toSchemaType()
            is GQLEnumTypeDefinition -> it.toSchemaType()
            is GQLUnionTypeDefinition -> it.toSchemaType()
          }
        }.toMap()
    )
  }

  private fun GQLObjectTypeDefinition.toSchemaType(): IntrospectionSchema.Type.Object {
    return IntrospectionSchema.Type.Object(
        name = name,
        description = description,
        fields = fields.map { it.toSchemaField() }
    )
  }

  private fun List<GQLDirective>.findDeprecationReason() = firstOrNull { it.name == "deprecated" }
      ?.let {
        it.arguments
            .firstOrNull { it.name == "reason" }
            ?.value
            ?.let { value ->
              if (value !is GQLStringValue) {
                throw ParseException("reason must be a string", it.sourceLocation)
              }
              value.value
            }
            ?: "No longer supported"
      }

  private fun GQLFieldDefinition.toSchemaField(): IntrospectionSchema.Field {
    val deprecationReason = directives.findDeprecationReason()
    return IntrospectionSchema.Field(
        name = name,
        description = description,
        isDeprecated = deprecationReason != null,
        deprecationReason = deprecationReason,
        type = type.toSchemaType(),
        args = arguments.map { it.toSchemaArgument() }
    )
  }

  private fun GQLDocument.typeDefinition(name: String): GQLTypeDefinition? {
    return definitions.filterIsInstance<GQLTypeDefinition>().firstOrNull { it.name == name }
  }

  private fun GQLType.toSchemaType(): IntrospectionSchema.TypeRef {
    return when (this) {
      is GQLNonNullType -> {
        IntrospectionSchema.TypeRef(
            kind = IntrospectionSchema.Kind.NON_NULL,
            name = null,
            ofType = type.toSchemaType()
        )
      }
      is GQLListType -> {
        IntrospectionSchema.TypeRef(
            kind = IntrospectionSchema.Kind.LIST,
            name = null,
            ofType = type.toSchemaType())
      }
      is GQLNamedType -> {
        val typeDefinition = typeDefinitions[name] ?: throw ParseException(
            message = "Undefined GraphQL schema type `$name`",
            sourceLocation = sourceLocation
        )
        IntrospectionSchema.TypeRef(
            kind = typeDefinition.schemaKind(),
            name = name,
            ofType = null
        )
      }
    }
  }

  private fun GQLDocument.rootOperationTypeName(operationType: String): String? {
    val schemaDefinition = definitions.filterIsInstance<GQLSchemaDefinition>()
        .firstOrNull()
    if (schemaDefinition == null) {
      // 3.3.1
      // No schema definition, look for an object type named after the operationType
      // i.e. Query, Mutation, ...
      return definitions.filterIsInstance<GQLObjectTypeDefinition>()
          .firstOrNull { it.name == operationType.capitalize() }
          ?.name
    }
    val namedType = schemaDefinition.rootOperationTypeDefinitions.firstOrNull {
      it.operationType == operationType
    }?.namedType

    return namedType
  }

  private fun GQLTypeDefinition.schemaKind() = when (this) {
    is GQLEnumTypeDefinition -> IntrospectionSchema.Kind.ENUM
    is GQLUnionTypeDefinition -> IntrospectionSchema.Kind.UNION
    is GQLObjectTypeDefinition -> IntrospectionSchema.Kind.OBJECT
    is GQLInputObjectTypeDefinition -> IntrospectionSchema.Kind.INPUT_OBJECT
    is GQLScalarTypeDefinition -> IntrospectionSchema.Kind.SCALAR
    is GQLInterfaceTypeDefinition -> IntrospectionSchema.Kind.INTERFACE
  }

  private fun GQLInputValueDefinition.toSchemaArgument(): IntrospectionSchema.Field.Argument {
    val deprecationReason = directives.findDeprecationReason()

    return IntrospectionSchema.Field.Argument(
        name = name,
        description = description,
        isDeprecated = deprecationReason != null,
        deprecationReason = deprecationReason,
        type = type.toSchemaType(),
        defaultValue = defaultValue?.toKotlinValue() // TODO: difference between null and absent
    )
  }

  private fun GQLValue.toKotlinValue(): Any? {
    return when (this) {
      is GQLIntValue -> value
      is GQLFloatValue -> value
      is GQLStringValue -> value
      is GQLNullValue -> null
      is GQLListValue -> values.map { it.toKotlinValue() }
      is GQLObjectValue -> fields.map { it.name to it.value.toKotlinValue() }.toMap()
      is GQLBooleanValue -> value
      is GQLEnumValue -> value // Could we use something else in Kotlin?
      is GQLVariableValue -> throw ParseException("Value cannot be a variable in a const context", sourceLocation)
    }
  }

  private fun GQLInputObjectTypeDefinition.toSchemaType(): IntrospectionSchema.Type.InputObject {
    return IntrospectionSchema.Type.InputObject(
        name = name,
        description = description,
        inputFields = inputFields.map { it.toSchemaInputField() }
    )
  }

  private fun GQLInputValueDefinition.toSchemaInputField(): IntrospectionSchema.InputField {
    val deprecationReason = directives.findDeprecationReason()
    return IntrospectionSchema.InputField(
        name = name,
        description = description,
        isDeprecated = deprecationReason != null,
        deprecationReason = deprecationReason,
        type = type.toSchemaType(),
        defaultValue = defaultValue?.toKotlinValue(),
    )
  }

  private fun GQLInterfaceTypeDefinition.toSchemaType(): IntrospectionSchema.Type.Interface {
    return IntrospectionSchema.Type.Interface(
        name = name,
        description = description,
        fields = fields.map { it.toSchemaField() },
        possibleTypes = typeDefinitions.values
            .filter { typeDefinition ->
              typeDefinition is GQLObjectTypeDefinition && typeDefinition.implementsInterfaces.contains(name)
            }
            .map { typeDefinition ->
              IntrospectionSchema.TypeRef(
                  kind = IntrospectionSchema.Kind.OBJECT,
                  name = typeDefinition.name
              )
            }
    )
  }

  private fun GQLEnumTypeDefinition.toSchemaType(): IntrospectionSchema.Type.Enum {
    return IntrospectionSchema.Type.Enum(
        name = name,
        description = description,
        enumValues = enumValues.map { it.toSchemaEnumValue() }
    )
  }

  private fun GQLEnumValueDefinition.toSchemaEnumValue(): IntrospectionSchema.Type.Enum.Value {
    val deprecationReason = directives.findDeprecationReason()
    return IntrospectionSchema.Type.Enum.Value(
        name = name,
        description = description,
        isDeprecated = deprecationReason != null,
        deprecationReason = deprecationReason
    )
  }

  private fun GQLScalarTypeDefinition.toSchemaType(): IntrospectionSchema.Type.Scalar {
    return IntrospectionSchema.Type.Scalar(
        name = name,
        description = description,
    )
  }

  private fun GQLUnionTypeDefinition.toSchemaType(): IntrospectionSchema.Type.Union {
    return IntrospectionSchema.Type.Union(
        name = name,
        description = description,
        fields = null,
        possibleTypes = memberTypes.map { it.toSchemaType() }
    )
  }
}

fun GQLDocument.toIntrospectionSchema() = IntrospectionSchemaBuilder(this).toIntrospectionSchema()

