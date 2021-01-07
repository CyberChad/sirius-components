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
package org.eclipse.sirius.web.graphql.datafetchers.mutation;

import java.util.Objects;

import org.eclipse.sirius.web.annotations.graphql.GraphQLMutationTypes;
import org.eclipse.sirius.web.annotations.spring.graphql.MutationDataFetcher;
import org.eclipse.sirius.web.graphql.datafetchers.IDataFetchingEnvironmentService;
import org.eclipse.sirius.web.graphql.schema.MutationTypeProvider;
import org.eclipse.sirius.web.services.api.dto.ErrorPayload;
import org.eclipse.sirius.web.services.api.dto.IPayload;
import org.eclipse.sirius.web.services.api.modelers.IModelerService;
import org.eclipse.sirius.web.services.api.modelers.PublishModelerInput;
import org.eclipse.sirius.web.services.api.modelers.PublishModelerSuccessPayload;
import org.eclipse.sirius.web.spring.graphql.api.IDataFetcherWithFieldCoordinates;

import graphql.schema.DataFetchingEnvironment;

/**
 * The data fetcher used to publish a modeler.
 * <p>
 * It will be used to handle the following GraphQL field:
 * </p>
 *
 * <pre>
 * type Mutation {
 *   publishModeler(input: PublishModelerInput!): PublishModelerPayload!
 * }
 * </pre>
 *
 * @author pcdavid
 */
// @formatter:off
@GraphQLMutationTypes(
    input = PublishModelerInput.class,
    payloads = {
        PublishModelerSuccessPayload.class
    }
)
@MutationDataFetcher(type = MutationTypeProvider.TYPE, field = MutationPublishModelerDataFetcher.PUBLISH_MODELER_FIELD)
// @formatter:on
public class MutationPublishModelerDataFetcher implements IDataFetcherWithFieldCoordinates<IPayload> {

    public static final String PUBLISH_MODELER_FIELD = "publishModeler"; //$NON-NLS-1$

    private final IDataFetchingEnvironmentService dataFetchingEnvironmentService;

    private final IModelerService modelerService;

    public MutationPublishModelerDataFetcher(IDataFetchingEnvironmentService dataFetchingEnvironmentService, IModelerService modelerService) {
        this.dataFetchingEnvironmentService = Objects.requireNonNull(dataFetchingEnvironmentService);
        this.modelerService = Objects.requireNonNull(modelerService);
    }

    @Override
    public IPayload get(DataFetchingEnvironment environment) throws Exception {
        var input = this.dataFetchingEnvironmentService.getInput(environment, PublishModelerInput.class);
        // @formatter:off
        return this.modelerService.publishModeler(input.getModelerId())
                                  .map(m -> (IPayload) new PublishModelerSuccessPayload(m))
                                  .orElse(new ErrorPayload("Unexpected error")); //$NON-NLS-1$
        // @formatter:on
    }
}
