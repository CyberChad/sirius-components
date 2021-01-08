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

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.sirius.web.persistence.repositories.IProjectRepository;
import org.eclipse.sirius.web.services.api.dto.ErrorPayload;
import org.eclipse.sirius.web.services.api.dto.IPayload;
import org.eclipse.sirius.web.services.api.modelers.CreateModelerInput;
import org.eclipse.sirius.web.services.api.modelers.CreateModelerSuccessPayload;
import org.eclipse.sirius.web.services.api.modelers.IModelerService;
import org.eclipse.sirius.web.services.api.modelers.Modeler;
import org.eclipse.sirius.web.services.api.modelers.PublicationStatus;
import org.eclipse.sirius.web.services.api.modelers.PublishModelerSuccessPayload;
import org.eclipse.sirius.web.services.api.modelers.RenameModelerSuccessPayload;
import org.eclipse.sirius.web.services.api.projects.Project;
import org.eclipse.sirius.web.spring.collaborative.messages.ICollaborativeMessageService;
import org.eclipse.sirius.web.spring.collaborative.projects.ProjectMapper;
import org.springframework.stereotype.Service;

/**
 * Service used to manipulate modelers.
 *
 * @author pcdavid
 */
@Service
public class ModelerService implements IModelerService {

    private final ICollaborativeMessageService messageService;

    private final IProjectRepository projectRepository;

    // TODO This is temporary, modelers should be persisted
    private final Map<UUID, Modeler> modelers = new LinkedHashMap<>();

    public ModelerService(ICollaborativeMessageService messageService, IProjectRepository projectRepository) {
        this.messageService = Objects.requireNonNull(messageService);
        this.projectRepository = Objects.requireNonNull(projectRepository);
    }

    @Override
    public IPayload createModeler(CreateModelerInput input) {
        IPayload payload = null;
        String name = input.getName().trim();
        if (!this.isValidModelerName(name)) {
            payload = new ErrorPayload(this.messageService.invalidModelerName());
        } else {
            var optionalProject = this.projectRepository.findById(input.getProjectId());
            if (optionalProject.isPresent()) {
                Modeler modeler = new Modeler(UUID.randomUUID(), name, new ProjectMapper().toDTO(optionalProject.get()), PublicationStatus.DRAFT);
                this.modelers.put(modeler.getId(), modeler);
                payload = new CreateModelerSuccessPayload(modeler);
            } else {
                payload = new ErrorPayload(this.messageService.projectNotFound());
            }
        }
        return payload;
    }

    @Override
    public IPayload renameModeler(UUID modelerId, String newName) {
        IPayload result;
        Modeler modeler = this.modelers.get(modelerId);
        if (modeler != null) {
            if (this.isValidModelerName(newName)) {
                Modeler newModeler = new Modeler(modeler.getId(), newName, modeler.getProject(), modeler.getStatus());
                this.modelers.put(modelerId, newModeler);
                result = new RenameModelerSuccessPayload(newModeler);
            } else {
                result = new ErrorPayload(this.messageService.invalidModelerName());
            }
        } else {
            result = new ErrorPayload(this.messageService.modelerNotFound());
        }
        return result;
    }

    @Override
    public IPayload publishModeler(UUID modelerId) {
        IPayload result;
        Modeler modeler = this.modelers.get(modelerId);
        if (modeler != null) {
            Modeler newModeler = new Modeler(modeler.getId(), modeler.getName(), modeler.getProject(), PublicationStatus.PUBLISHED);
            this.modelers.put(modelerId, newModeler);
            result = new PublishModelerSuccessPayload(newModeler);
        } else {
            result = new ErrorPayload(this.messageService.modelerNotFound());
        }
        return result;
    }

    @Override
    public List<Modeler> getModelers(Project parentProject) {
        // @formatter:off
        return this.modelers.values().stream()
                   .filter(modeler -> Objects.equals(modeler.getProject().getId(), parentProject.getId()))
                   .sorted(Comparator.comparing(Modeler::getName))
                   .collect(Collectors.toList());
        // @formatter:on
    }

    private boolean isValidModelerName(String name) {
        return 3 <= name.length() && name.length() <= 20;
    }
}
