/*******************************************************************************
 * Copyright (c) 2021.
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
import { makeStyles, Typography } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import React from 'react';
import { useParams } from 'react-router-dom';
import { View } from 'views/View';

const useStyles = makeStyles((theme) => ({
  modelersEmptyView: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
  },

  emptyContainer: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'repeat(3, min-content)',
    rowGap: theme.spacing(2),
    justifyItems: 'center',
    alignItems: 'center',
  },

  actions: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: theme.spacing(3),
  },
}));

export const ModelersEmptyView = () => {
  const { projectId } = useParams();
  const classes = useStyles();
  return (
    <View>
      <div className={classes.modelersEmptyView}>
        <div className={classes.emptyContainer}>
          <Typography variant="h4">There are no modelers yet</Typography>
          <Typography variant="subtitle1">Start creating your first modeler and it will appear here</Typography>
          <div className={classes.actions}>
            <Button
              data-testid="create"
              color="primary"
              href={`/projects/${projectId}/new/modeler`}
              variant="contained">
              New
            </Button>
          </div>
        </div>
      </div>
    </View>
  );
};
