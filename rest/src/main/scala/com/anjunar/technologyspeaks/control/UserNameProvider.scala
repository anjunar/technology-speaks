package com.anjunar.technologyspeaks.control

import com.anjunar.technologyspeaks.jaxrs.search.{Context, PredicateProvider}
import com.anjunar.technologyspeaks.shared.property.ManagedProperty
import com.google.common.base.Strings
import jakarta.persistence.criteria.JoinType

import java.lang

class UserNameProvider extends PredicateProvider[String, User ]{

  override def build(context: Context[String, User]): Unit = {
    val Context(value, entityManager, builder, predicates, root, query, selection, property, name, parameters) = context

    if (! Strings.isNullOrEmpty(value)) {

      val currentUser = User.current()
      val credential = Credential.current()

      parameters.put(name, context.value)

      val distanceExpr = builder.function(
        "similarity",
        classOf[java.lang.Double],
        root.get("info").get(name),
        builder.parameter(classOf[String], name)
      )
      val predicate = builder.greaterThan(distanceExpr, builder.literal[lang.Double](0.3d))

      selection.addOne(distanceExpr)

      if (credential.hasRole("Administrator")) {
        predicates.addOne(predicate)
      } else {
        val subquery = query.subquery(classOf[Long])
        val propertyRoot = subquery.from(classOf[ManagedProperty])
        val viewJoin = propertyRoot.join("view")
        val subUser = viewJoin.get("user")

        val subGroups = propertyRoot.joinSet("groups", JoinType.LEFT).joinSet("users", JoinType.LEFT)
        val subUsers = propertyRoot.joinSet("users", JoinType.LEFT)

        val visibleForAllPredicate = builder.isTrue(propertyRoot.get("visibleForAll"))
        val propertyName = builder.equal(propertyRoot.get("value"), "info")
        val userInUsersPredicate = builder.equal(subUsers, currentUser)
        val userGroupPredicate = builder.equal(subGroups, currentUser)

        subquery.select(builder.literal(1L)).where(
          builder.equal(subUser, root),
          propertyName,
          builder.or(
            visibleForAllPredicate,
            userInUsersPredicate,
            userGroupPredicate
          )
        )

        predicates.addOne(builder.and(Array(builder.exists(subquery), predicate) *))

      }
    }


  }
}
