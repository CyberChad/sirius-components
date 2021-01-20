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
import org.eclipse.sirius.web.collaborative.api.services.IEditingContextEventHandler;
import org.eclipse.sirius.web.core.api.ErrorPayload;
import org.eclipse.sirius.web.core.api.IEditingContext;
import org.eclipse.sirius.web.core.api.IInput;
import org.eclipse.sirius.web.core.api.IPayload;
import org.eclipse.sirius.web.services.api.modelers.IModelerService;
import org.eclipse.sirius.web.services.api.modelers.RenameModelerInput;
import org.eclipse.sirius.web.spring.collaborative.messages.ICollaborativeMessageService;
import org.springframework.stereotype.Service;

/**
 * Handler used to rename a modeler.
 *
 * @author pcdavid
 */
@Service
public class RenameModelerEventHandler implements IEditingContextEventHandler {

    private final ICollaborativeMessageService messageService;

    private final IModelerService modelerService;

    public RenameModelerEventHandler(ICollaborativeMessageService messageService, IModelerService modelerService) {
        this.messageService = Objects.requireNonNull(messageService);
        this.modelerService = Objects.requireNonNull(modelerService);
    }

    @Override
    public boolean canHandle(IInput input) {
        return input instanceof RenameModelerInput;
    }

    @Override
    public EventHandlerResponse handle(IEditingContext editingContext, IInput input) {
        final IPayload payload;
        if (input instanceof RenameModelerInput) {
            RenameModelerInput renameModelerInput = (RenameModelerInput) input;
            payload = this.modelerService.renameModeler(renameModelerInput.getModelerId(), renameModelerInput.getNewName());
        } else {
            String message = this.messageService.invalidInput(input.getClass().getSimpleName(), RenameModelerInput.class.getSimpleName());
            payload = new ErrorPayload(message);
        }
        return new EventHandlerResponse(false, representation -> false, payload);
    }

}
