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
import React, { useState } from 'react';
import { MemoryRouter } from 'react-router-dom';

import { NewRepresentationModal } from 'modals/new-representation/NewRepresentationModal';
import { Border } from 'stories/common/Border';
import { View } from 'views/View';

export const NewRepresentationModalStory = () => {
  const [state, setState] = useState(true);

  let modal;
  if (state) {
    modal = (
      <NewRepresentationModal
        projectId=""
        objectId=""
        onRepresentationCreated={() => {}}
        onClose={() => setState(false)}
      />
    );
  }

  return (
    <Border>
      <MemoryRouter>
        <View>
          <div>{modal}</div>
        </View>
      </MemoryRouter>
    </Border>
  );
};
