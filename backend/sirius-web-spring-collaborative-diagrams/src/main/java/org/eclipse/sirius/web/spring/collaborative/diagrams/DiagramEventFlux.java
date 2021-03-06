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
package org.eclipse.sirius.web.spring.collaborative.diagrams;

import java.util.Objects;

import org.eclipse.sirius.web.collaborative.api.dto.PreDestroyPayload;
import org.eclipse.sirius.web.collaborative.diagrams.api.dto.DiagramRefreshedEventPayload;
import org.eclipse.sirius.web.core.api.IPayload;
import org.eclipse.sirius.web.diagrams.Diagram;

import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

/**
 * Service used to manage the diagram event flux.
 *
 * @author sbegaudeau
 */
public class DiagramEventFlux {

    private final DirectProcessor<IPayload> flux;

    private final FluxSink<IPayload> sink;

    private final Diagram initialDiagram;

    public DiagramEventFlux(Diagram initialDiagram) {
        this.flux = DirectProcessor.create();
        this.sink = this.flux.sink();
        this.initialDiagram = Objects.requireNonNull(initialDiagram);
    }

    public void diagramRefreshed(Diagram newDiagram) {
        this.sink.next(new DiagramRefreshedEventPayload(newDiagram));
    }

    public Flux<IPayload> getFlux() {
        var initialRefresh = Mono.fromCallable(() -> new DiagramRefreshedEventPayload(this.initialDiagram));
        return Flux.concat(initialRefresh, this.flux);
    }

    public void dispose() {
        this.flux.onComplete();
    }

    public void preDestroy() {
        this.sink.next(new PreDestroyPayload(this.initialDiagram.getId()));
    }
}
