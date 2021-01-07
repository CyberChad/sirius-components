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
import Button from '@material-ui/core/Button';
import Paper from '@material-ui/core/Paper';
import { makeStyles } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';
import PropTypes from 'prop-types';
import React from 'react';
import { useParams } from 'react-router-dom';

const propTypes = {
  children: PropTypes.node.isRequired,
};

const useStyles = makeStyles((theme) => ({
  modelersViewContainer: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: 'min-content 1fr',
    rowGap: theme.spacing(4),
  },

  header: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: theme.spacing(5),
  },

  actions: {
    display: 'grid',
    gridTemplateRows: '1fr',
    gridTemplateColumns: 'min-content',
    columnGap: theme.spacing(2),
  },

  content: {
    display: 'grid',
    gridTemplateColumns: '1fr',
    gridTemplateRows: '1fr',
  },
}));

export const ModelersViewContainer = ({ children }) => {
  const { projectId } = useParams();
  const classes = useStyles();
  return (
    <div className={classes.modelersViewContainer}>
      <div className={classes.header}>
        <Typography variant="h5">Modelers</Typography>
        <div className={classes.actions}>
          <Button data-testid="create" color="primary" href={`/projects/${projectId}/new/modeler`} variant="contained">
            New
          </Button>
        </div>
      </div>
      <Paper className={classes.content}>{children}</Paper>
    </div>
  );
};
ModelersViewContainer.propTypes = propTypes;
