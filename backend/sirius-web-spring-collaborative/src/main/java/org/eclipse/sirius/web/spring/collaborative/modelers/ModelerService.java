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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.eclipse.sirius.web.services.api.dto.ErrorPayload;
import org.eclipse.sirius.web.services.api.dto.IPayload;
import org.eclipse.sirius.web.services.api.modelers.CreateModelerInput;
import org.eclipse.sirius.web.services.api.modelers.CreateModelerSuccessPayload;
import org.eclipse.sirius.web.services.api.modelers.IModelerService;
import org.eclipse.sirius.web.services.api.modelers.Modeler;
import org.eclipse.sirius.web.services.api.modelers.PublicationStatus;
import org.eclipse.sirius.web.services.api.projects.IProjectService;
import org.eclipse.sirius.web.services.api.projects.Project;
import org.eclipse.sirius.web.spring.collaborative.messages.ICollaborativeMessageService;
import org.springframework.stereotype.Service;

/**
 * Service used to manipulate modelers.
 *
 * @author pcdavid
 */
@Service
public class ModelerService implements IModelerService {

    private final ICollaborativeMessageService messageService;

    private final IProjectService projectService;

    // TODO This is temporary, modelers should be persisted
    private final Map<UUID, Modeler> modelers = new LinkedHashMap<>();

    public ModelerService(ICollaborativeMessageService messageService, IProjectService projectService) {
        this.messageService = Objects.requireNonNull(messageService);
        this.projectService = Objects.requireNonNull(projectService);
    }

    @Override
    public IPayload createModeler(CreateModelerInput input) {
        IPayload payload = null;
        String name = input.getName().trim();
        if (!this.isValidModelerName(name)) {
            payload = new ErrorPayload(this.messageService.invalidModelerName());
        } else {
            var optionalParentProject = this.projectService.getProject(input.getParentProjectId());
            if (optionalParentProject.isPresent()) {
                Modeler modeler = new Modeler(UUID.randomUUID(), name, optionalParentProject.get(), PublicationStatus.DRAFT);
                this.modelers.put(modeler.getId(), modeler);
                payload = new CreateModelerSuccessPayload(modeler);
            } else {
                payload = new ErrorPayload(this.messageService.projectNotFound());
            }
        }
        return payload;
    }

    @Override
    public Optional<Modeler> renameModeler(UUID modelerId, String newName) {
        return this.updateModeler(modelerId, modeler -> {
            if (this.isValidModelerName(newName)) {
                return new Modeler(modeler.getId(), newName, modeler.getParentProject(), modeler.getStatus());
            } else {
                return modeler;
            }
        });
    }

    @Override
    public Optional<Modeler> publishModeler(UUID modelerId) {
        return this.updateModeler(modelerId, modeler -> new Modeler(modeler.getId(), modeler.getName(), modeler.getParentProject(), PublicationStatus.PUBLISHED));
    }

    private Optional<Modeler> updateModeler(UUID modelerId, UnaryOperator<Modeler> updater) {
        Modeler modeler = this.modelers.get(modelerId);
        if (modeler != null) {
            Modeler newModeler = updater.apply(modeler);
            this.modelers.put(modelerId, newModeler);
            return Optional.of(newModeler);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Modeler> getAllModelers() {
        return List.copyOf(this.modelers.values());
    }

    @Override
    public List<Modeler> getModelers(Project parentProject) {
        return this.modelers.values().stream().filter(modeler -> Objects.equals(modeler.getParentProject().getId(), parentProject.getId())).collect(Collectors.toList());
    }

    private boolean isValidModelerName(String name) {
        return 3 <= name.length() && name.length() <= 20;
    }
}
