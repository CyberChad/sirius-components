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
package org.eclipse.sirius.web.spring.collaborative.modelers;

import java.util.Objects;

import org.eclipse.sirius.web.collaborative.api.services.EventHandlerResponse;
import org.eclipse.sirius.web.collaborative.api.services.IProjectEventHandler;
import org.eclipse.sirius.web.services.api.Context;
import org.eclipse.sirius.web.services.api.dto.ErrorPayload;
import org.eclipse.sirius.web.services.api.dto.IPayload;
import org.eclipse.sirius.web.services.api.dto.IProjectInput;
import org.eclipse.sirius.web.services.api.modelers.IModelerService;
import org.eclipse.sirius.web.services.api.modelers.PublishModelerInput;
import org.eclipse.sirius.web.services.api.objects.IEditingContext;
import org.eclipse.sirius.web.spring.collaborative.messages.ICollaborativeMessageService;
import org.springframework.stereotype.Service;

/**
 * Handler used to publis a modeler.
 *
 * @author pcdavid
 */
@Service
public class PublishModelerEventHandler implements IProjectEventHandler {

    private final ICollaborativeMessageService messageService;

    private final IModelerService modelerService;

    public PublishModelerEventHandler(ICollaborativeMessageService messageService, IModelerService modelerService) {
        this.messageService = Objects.requireNonNull(messageService);
        this.modelerService = Objects.requireNonNull(modelerService);
    }

    @Override
    public boolean canHandle(IProjectInput projectInput) {
        return projectInput instanceof PublishModelerInput;
    }

    @Override
    public EventHandlerResponse handle(IEditingContext editingContext, IProjectInput projectInput, Context context) {
        final IPayload payload;
        if (projectInput instanceof PublishModelerInput) {
            PublishModelerInput input = (PublishModelerInput) projectInput;
            payload = this.modelerService.publishModeler(input.getModelerId());
        } else {
            String message = this.messageService.invalidInput(projectInput.getClass().getSimpleName(), PublishModelerInput.class.getSimpleName());
            payload = new ErrorPayload(message);
        }
        return new EventHandlerResponse(false, representation -> false, payload);
    }

}
