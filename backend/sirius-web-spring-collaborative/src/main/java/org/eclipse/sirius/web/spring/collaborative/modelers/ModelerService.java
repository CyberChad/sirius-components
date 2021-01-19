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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.sirius.web.persistence.entities.ModelerEntity;
import org.eclipse.sirius.web.persistence.entities.PublicationStatusEntity;
import org.eclipse.sirius.web.persistence.repositories.IModelerRepository;
import org.eclipse.sirius.web.persistence.repositories.IProjectRepository;
import org.eclipse.sirius.web.services.api.dto.ErrorPayload;
import org.eclipse.sirius.web.services.api.dto.IPayload;
import org.eclipse.sirius.web.services.api.modelers.CreateModelerInput;
import org.eclipse.sirius.web.services.api.modelers.CreateModelerSuccessPayload;
import org.eclipse.sirius.web.services.api.modelers.IModelerService;
import org.eclipse.sirius.web.services.api.modelers.Modeler;
import org.eclipse.sirius.web.services.api.modelers.PublishModelerSuccessPayload;
import org.eclipse.sirius.web.services.api.modelers.RenameModelerSuccessPayload;
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

    private final IProjectRepository projectRepository;

    private final IModelerRepository modelerRepository;

    public ModelerService(ICollaborativeMessageService messageService, IProjectRepository projectRepository, IModelerRepository modelerRepository) {
        this.messageService = Objects.requireNonNull(messageService);
        this.projectRepository = Objects.requireNonNull(projectRepository);
        this.modelerRepository = Objects.requireNonNull(modelerRepository);
    }

    @Override
    public IPayload createModeler(CreateModelerInput input) {
        var optionalProjectEntity = this.projectRepository.findById(input.getProjectId());
        return optionalProjectEntity.map(projectEntity -> {
            String name = input.getName().trim();
            if (!this.isValidModelerName(name)) {
                return new ErrorPayload(this.messageService.invalidModelerName());
            } else {
                ModelerEntity modelerEntity = new ModelerEntity();
                modelerEntity.setName(input.getName());
                modelerEntity.setProject(optionalProjectEntity.get());
                modelerEntity.setPublicationStatus(PublicationStatusEntity.DRAFT);

                modelerEntity = this.modelerRepository.save(modelerEntity);
                return new CreateModelerSuccessPayload(this.toDTO(modelerEntity));
            }
        }).orElse(new ErrorPayload(this.messageService.projectNotFound()));

    }

    @Override
    public IPayload renameModeler(UUID modelerId, String newName) {
        var optionalModelerEntity = this.modelerRepository.findById(modelerId);
        return optionalModelerEntity.map(modelerEntity -> {
            if (this.isValidModelerName(newName)) {
                modelerEntity.setName(newName);
                modelerEntity = this.modelerRepository.save(modelerEntity);
                return new RenameModelerSuccessPayload(this.toDTO(modelerEntity));
            } else {
                return new ErrorPayload(this.messageService.invalidModelerName());
            }
        }).orElse(new ErrorPayload(this.messageService.modelerNotFound()));
    }

    @Override
    public IPayload publishModeler(UUID modelerId) {
        var optionalModelerEntity = this.modelerRepository.findById(modelerId);
        return optionalModelerEntity.map(modelerEntity -> {
            modelerEntity.setPublicationStatus(PublicationStatusEntity.PUBLISHED);
            modelerEntity = this.modelerRepository.save(modelerEntity);
            return (IPayload) new PublishModelerSuccessPayload(this.toDTO(modelerEntity));
        }).orElse(new ErrorPayload(this.messageService.modelerNotFound()));
    }

    @Override
    public Optional<Modeler> getModeler(UUID modelerId) {
        return this.modelerRepository.findById(modelerId).map(this::toDTO);
    }

    @Override
    public List<Modeler> getModelers(Project project) {
        // @formatter:off
        return this.modelerRepository.findAllByProjectId(project.getId()).stream()
                                     .map(this::toDTO)
                                     .sorted(Comparator.comparing(Modeler::getName))
                                     .collect(Collectors.toList());
        // @formatter:on
    }

    private Modeler toDTO(ModelerEntity modelerEntity) {
        return new ModelerMapper().toDTO(modelerEntity);
    }

    private boolean isValidModelerName(String name) {
        return 3 <= name.length() && name.length() <= 20;
    }
}
