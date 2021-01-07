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
package org.eclipse.sirius.web.services.api.modelers;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.web.services.api.projects.Project;

/**
 * DTO representing a modeler.
 *
 * @author pcdavid
 */
public class Modeler {
    private final UUID id;

    private final String name;

    private final Project parentProject;

    private final PublicationStatus publicationStatus;

    public Modeler(UUID id, String name, Project parentProject, PublicationStatus publicationStatus) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.parentProject = Objects.requireNonNull(parentProject);
        this.publicationStatus = Objects.requireNonNull(publicationStatus);
    }

    public UUID getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Project getParentProject() {
        return this.parentProject;
    }

    public PublicationStatus getStatus() {
        return this.publicationStatus;
    }

    @Override
    public String toString() {
        String pattern = "{0} '{'id: {1}, name: {2}, parentProject: { id: {3}, name: {4} }, publicationStatus: {5}'}'"; //$NON-NLS-1$
        // @formatter:off
        return MessageFormat.format(pattern, this.getClass().getSimpleName(), this.id, this.name,
                                    this.parentProject.getId(), this.getParentProject().getName(),
                                    this.publicationStatus);
        // @formatter:on
    }

}
