/*******************************************************************************
 * Copyright (c) 2019, 2020 Obeo.
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
package org.eclipse.sirius.web.spring.collaborative.representations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.sirius.web.api.configuration.IPropertiesDescriptionRegistry;
import org.eclipse.sirius.web.forms.description.FormDescription;

/**
 * Registry containing all the properties descriptions.
 *
 * @author hmarchadour
 */
public class PropertiesDescriptionRegistry implements IPropertiesDescriptionRegistry {

    private final Map<UUID, FormDescription> id2propertiesDescriptions = new HashMap<>();

    @Override
    public void add(FormDescription formDescription) {
        this.id2propertiesDescriptions.put(formDescription.getId(), formDescription);
    }

    public Optional<FormDescription> getPropertiesDescription(UUID id) {
        return Optional.ofNullable(this.id2propertiesDescriptions.get(id));
    }

    public List<FormDescription> getPropertiesDescriptions() {
        return this.id2propertiesDescriptions.values().stream().collect(Collectors.toList());
    }

}
