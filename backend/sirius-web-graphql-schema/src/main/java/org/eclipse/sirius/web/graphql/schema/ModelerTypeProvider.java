/*******************************************************************************
 * Copyright (c) 2021 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.graphql.schema;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.sirius.web.graphql.utils.providers.GraphQLEnumTypeProvider;
import org.eclipse.sirius.web.graphql.utils.schema.ITypeProvider;
import org.eclipse.sirius.web.services.api.modelers.PublicationStatus;
import org.springframework.stereotype.Service;

import graphql.Scalars;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;

/**
 * Defines the Modeler type and its related type for the GrahQL Schema definition.
 *
 * <p>
 * The types created will match the following GraphQL textual definition:
 * </p>
 *
 * <pre>
 * type Modeler {
 *   id: ID!
 *   name: String!
 *   status: PublicationStatus!
 * }
 * </pre>
 *
 * @author pcdavid
 */
@Service
public class ModelerTypeProvider implements ITypeProvider {
    public static final String TYPE = "Modeler"; //$NON-NLS-1$

    public static final String NAME_FIELD = "name"; //$NON-NLS-1$

    public static final String STATUS_FIELD = "status"; //$NON-NLS-1$

    private final GraphQLEnumTypeProvider graphQLEnumTypeProvider = new GraphQLEnumTypeProvider();

    @Override
    public Set<GraphQLType> getTypes() {
        // @formatter:off
        GraphQLObjectType modelerType = GraphQLObjectType.newObject()
                .name(TYPE)
                .field(new IdFieldProvider().getField())
                .field(this.getNameField())
                .field(this.getStatusField())
                .build();
        // @formatter:on

        GraphQLEnumType satusType = this.graphQLEnumTypeProvider.getType(PublicationStatus.class);

        Set<GraphQLType> types = new LinkedHashSet<>();
        types.add(modelerType);
        types.add(satusType);

        return types;
    }

    private GraphQLFieldDefinition getNameField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(NAME_FIELD)
                .type(new GraphQLNonNull(Scalars.GraphQLString))
                .build();
        // @formatter:on
    }

    private GraphQLFieldDefinition getStatusField() {
        // @formatter:off
        return GraphQLFieldDefinition.newFieldDefinition()
                .name(STATUS_FIELD)
                .type(new GraphQLNonNull(new GraphQLTypeReference(PublicationStatus.class.getSimpleName())))
                .build();
        // @formatter:on
    }

}
