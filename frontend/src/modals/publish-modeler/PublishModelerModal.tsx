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
import { useMutation } from '@apollo/client';
import { Checkbox } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import FormGroup from '@material-ui/core/FormGroup';
import gql from 'graphql-tag';
import PropTypes from 'prop-types';
import React, { useEffect, useState } from 'react';
const publishModelerMutation = gql`
  mutation publishModeler($input: PublishModelerInput!) {
    publishModeler(input: $input) {
      __typename
      ... on PublishModelerSuccessPayload {
        modeler {
          id
          name
          status
        }
      }
      ... on ErrorPayload {
        message
      }
    }
  }
`;

const propTypes = {
  modelerId: PropTypes.string.isRequired,
  onModelerPublished: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
};

export const PublishModelerModal = ({ modelerId, onModelerPublished, onClose }) => {
  const initialState = {
    confirmed: false,
    error: '',
  };
  const [state, setState] = useState(initialState);

  const onToggleConfirmation = (event: React.ChangeEvent<HTMLInputElement>) => {
    setState(() => {
      return { confirmed: event.target.checked, error: '' };
    });
  };

  const [publishModeler, { loading, data, error }] = useMutation(publishModelerMutation);
  useEffect(() => {
    if (!loading) {
      if (error) {
        setState((_) => {
          return { confirmed: false, error: error.message };
        });
      } else if (data?.publishModeler) {
        const { publishModeler } = data;
        if (publishModeler.__typename === 'PublishModelerSuccessPayload') {
          onModelerPublished();
        } else if (publishModeler.__typename === 'ErrorPayload') {
          const error = publishModeler.message;
          setState((_) => {
            return { confirmed: false, error };
          });
        }
      }
    }
  }, [loading, data, error, onModelerPublished]);

  const onPublishModeler = (event) => {
    event.preventDefault();
    const input = {
      modelerId: modelerId,
    };
    publishModeler({ variables: { input } });
  };

  return (
    <Dialog open={true} onClose={onClose} aria-labelledby="form-dialog-title">
      <DialogTitle id="dialog-title">Publish the modeler</DialogTitle>
      <DialogContent>
        <DialogContentText>
          The new version will automatically apply to all user data which uses this modeler.
          <br /> Are you sure?
        </DialogContentText>
        <FormGroup row>
          <FormControlLabel
            control={
              <Checkbox checked={state.confirmed} onChange={onToggleConfirmation} name="publication-confirmed" />
            }
            label="Yes I am sure"
          />
        </FormGroup>
      </DialogContent>
      <DialogActions>
        <Button
          variant="contained"
          disabled={!state.confirmed}
          onClick={onPublishModeler}
          color="primary"
          data-testid="rename-modeler">
          Publish
        </Button>
      </DialogActions>
    </Dialog>
  );
};
PublishModelerModal.propTypes = propTypes;
